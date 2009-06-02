/*******************************************************************************
 * Copyright (c) 2008, 2009 Ivan Egorov <egorich.3.04@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ivan Egorov <egorich.3.04@gmail.com>
 *******************************************************************************/

package com.google.code.annatasha.validator.internal.build;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.Assignment.Operator;

import com.google.code.annatasha.validator.internal.build.markers.AstNodeMarkerFactory;
import com.google.code.annatasha.validator.internal.build.symbols.FieldInformation;
import com.google.code.annatasha.validator.internal.build.symbols.MethodInformation;
import com.google.code.annatasha.validator.internal.build.symbols.Permissions;
import com.google.code.annatasha.validator.internal.build.symbols.TypeInformation;

class MethodBodyVerifier extends ASTVisitor {

	private final IModelResolver resolver;
	private final IReportListener listener;

	private boolean readAccessFlag;
	private boolean writeAccessFlag;
	private boolean parameterFlag;

	private ITypeBinding type;
	private boolean isThreadStarter;

	private MethodInformation method;

	private IResource resource;

	public MethodBodyVerifier(IModelResolver resolver, IReportListener listener) {
		this.resolver = resolver;
		this.listener = listener;
	}

	public void validate(IResource resource, MethodInformation method,
			ASTNode methodBody) {
		this.resource = resource;
		this.method = method;

		methodBody.accept(this);
	}

	// STOP METHODS
	// (we should not process anonymous and inline classes declarations during
	// method parsing)
	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		return false;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		return false;
	}

	// EXPRESSION PROCESSING METHODS
	@Override
	public boolean visit(ArrayAccess node) {
		verify(node.getArray(), readAccessFlag, writeAccessFlag, false);
		verify(node.getIndex(), true, false, false);
		type = node.resolveTypeBinding();
		return false;
	}

	@Override
	public boolean visit(AssertStatement node) {
		verify(node.getExpression(), true, false, false);
		if (node.getMessage() != null) {
			verify(node.getMessage(), true, false, false);
		}
		return false;
	}

	@Override
	public boolean visit(Assignment node) {
		verify(node.getLeftHandSide(), node.getOperator() != Operator.ASSIGN,
				true, false);
		ITypeBinding lhs = type;
		boolean lts = isThreadStarter;

		verify(node.getRightHandSide(), true, false, false);
		ITypeBinding rhs = type;
		boolean rts = isThreadStarter;

		validateCast(node, rhs, rts, lhs, lts);
		return false;
	}

	@Override
	public boolean visit(CastExpression node) {
		verify(node.getExpression(), readAccessFlag, writeAccessFlag,
				parameterFlag);
		ITypeBinding lhs = type;
		boolean lts = isThreadStarter;

		ITypeBinding rhs = node.resolveTypeBinding();
		boolean rts = isTask(lhs) && !isTask(rhs);

		validateCast(node, rhs, rts, lhs, lts);

		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(ClassInstanceCreation node) {
		processMethodInvocation(node, node.resolveConstructorBinding(), node
				.arguments());

		return false;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		if (node.getInitializer() != null) {
			verify(node.getInitializer(), true, false, false);
		}
		return false;
	}

	@Override
	public boolean visit(FieldAccess node) {
		verify(node.getExpression(), true, false, false);
		if (isThreadStarter) {
			reportProblem(node.getExpression(),
					Error.ThreadStarterCannotBePartOfExpression);
		}

		checkAccessPolicy(node, node.resolveFieldBinding(), readAccessFlag,
				writeAccessFlag, parameterFlag);

		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MethodInvocation node) {
		if (node.getExpression() != null) {
			verify(node.getExpression(), true, false, false);
			if (isThreadStarter) {
				reportProblem(node.getExpression(),
						Error.ThreadStarterCannotBePartOfExpression);
			}
		}

		IMethodBinding binding = node.resolveMethodBinding();
		processMethodInvocation(node, binding, node.arguments());

		return false;
	}

	private void processMethodInvocation(ASTNode node, IMethodBinding binding,
			List<Expression> params) {
		ITypeBinding[] parameterTypes = binding.getParameterTypes();

		MethodInformation info = resolver.getMethodInformation(KeysFactory
				.getKey(binding.getMethodDeclaration()));

		if (info == null) {
			// reportProblem(node, Error.SymbolUndefined);
		} else {
			if (!Permissions.mightAccess(resolver, method.getExecPermissions(),
					info.getExecPermissions())) {
				reportProblem(node,
						Error.MethodAttemptsToExecInaccessibleMethod);
			}
		}
		int threadStartersCount = info == null || info.threadStarters == null ? 0
				: info.threadStarters.size();

		int i = 0;
		int cursor = 0;
		for (Expression param : params) {
			verify(param, true, false, true);

			boolean rts = false;
			if (cursor < threadStartersCount
					&& info.threadStarters.get(cursor) == i) {
				rts = true;
				++cursor;
			}
			validateCast(node, parameterTypes[i], rts, type, isThreadStarter);

			++i;
		}

		type = binding.isConstructor() ? ModelValidator
				.getCorrectBinding(binding.getDeclaringClass())
				: ModelValidator.getCorrectBinding(binding.getReturnType());
		isThreadStarter = false;
	}

	@Override
	public boolean visit(QualifiedName node) {
		IBinding binding = node.resolveBinding();
		if (binding instanceof IVariableBinding) {
			IVariableBinding var = (IVariableBinding) binding;
			verify(node.getQualifier(), true, false, false);
			if (isThreadStarter) {
				reportProblem(node.getQualifier(),
						Error.ThreadStarterCannotBePartOfExpression);
			}

			checkAccessPolicy(node, var, readAccessFlag, writeAccessFlag,
					parameterFlag);
		}
		return false;
	}

	@Override
	public boolean visit(SimpleName node) {
		IBinding binding = node.resolveBinding();
		if (binding instanceof IVariableBinding) {
			IVariableBinding var = (IVariableBinding) binding;
			checkAccessPolicy(node, var, readAccessFlag, writeAccessFlag,
					parameterFlag);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(SuperConstructorInvocation node) {
		processMethodInvocation(node, node.resolveConstructorBinding(), node
				.arguments());
		return false;
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		IVariableBinding var = node.resolveFieldBinding();
		checkAccessPolicy(node, var, readAccessFlag, writeAccessFlag,
				parameterFlag);
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(SuperMethodInvocation node) {
		processMethodInvocation(node, node.resolveMethodBinding(), node
				.arguments());
		return false;
	}

	@Override
	public boolean visit(ThisExpression node) {
		type = node.resolveTypeBinding();
		isThreadStarter = false;
		return false;
	}

	@Override
	public boolean visit(PostfixExpression node) {
		verify(node.getOperand(), true, true, false);
		return false;
	}

	@Override
	public boolean visit(PrefixExpression node) {
		verify(node.getOperand(), true, true, false);
		return false;
	}

	private void verify(Expression node, boolean rFlag, boolean wFlag,
			boolean paramFlag) {
		boolean oldR = readAccessFlag;
		boolean oldW = writeAccessFlag;
		boolean oldParam = parameterFlag;

		readAccessFlag = rFlag;
		writeAccessFlag = wFlag;
		parameterFlag = paramFlag;
		try {
			node.accept(this);
		} finally {
			readAccessFlag = oldR;
			writeAccessFlag = oldW;
			parameterFlag = oldParam;
		}
	}

	/**
	 * @param node
	 *            TODO
	 * @param binding
	 * @param parameterFlag
	 * @param writeAccessFlag
	 * @param readAccessFlag
	 */
	private void checkAccessPolicy(ASTNode node, IVariableBinding binding,
			boolean readAccessFlag, boolean writeAccessFlag,
			boolean parameterFlag) {
		if (binding.isField()) {
			FieldInformation info = resolver.getFieldInformation(KeysFactory
					.getKey(binding));
			if (info == null) {
				// reportProblem(node, Error.SymbolUndefined);
			} else {
				if (readAccessFlag) {
					if (!Permissions.mightAccess(resolver, method
							.getExecPermissions(), info.getReadPermissions())) {
						reportProblem(node,
								Error.MethodAttemptsToReadInaccessibleVariable);
					}
				}

				if (writeAccessFlag
						|| (parameterFlag && binding.getType().isArray())) {
					if (!Permissions.mightAccess(resolver, method
							.getExecPermissions(), info.getWritePermissions())) {
						reportProblem(node,
								Error.MethodAttemptsToWriteInaccessibleVariable);
					}
				}
			}
			type = ModelValidator.getCorrectBinding(binding.getType());
			isThreadStarter = info == null ? false : info.threadStarter.value;
		} else if (binding.isParameter()) {
			FieldInformation info = resolver.getFieldInformation(KeysFactory
					.getKey(binding));

			type = ModelValidator.getCorrectBinding(binding.getType());
			isThreadStarter = info == null ? false : info.threadStarter.value;
		}
	}

	private void validateCast(ASTNode node, ITypeBinding dest, boolean destTS,
			ITypeBinding src, boolean srcTS) {
		boolean srcTask = isTask(src);
		boolean destTask = isTask(dest);
		if (srcTask && !destTask) {
			// task information lost
			if (!destTS) {
				reportProblem(node, Error.InvalidTypeUpCast);
			}
		} else if (!srcTask && destTask) {
			// new task information
			reportProblem(node, Error.InvalidTypeDownCast);
		} else if (srcTS && !destTS) {
			reportProblem(node, Error.InvalidAssignment);
		}

		type = dest;
		isThreadStarter = destTS;
	}

	private boolean isTask(ITypeBinding rhs) {
		TypeInformation information = resolver.getTypeInformation(KeysFactory
				.getKey(ModelValidator.getCorrectBinding(rhs)));
		if (information != null) {
			return information.threadStarter;
		}
		return false;
	}

	private void reportProblem(ASTNode node, Error error) {
		listener.reportProblem(new AstNodeMarkerFactory(resource, node), error);
	}

}