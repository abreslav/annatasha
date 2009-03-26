package com.google.code.annatasha.validator.internal.build;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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

import com.google.code.annatasha.validator.internal.build.AnnatashaValidationResolver.Error;
import com.google.code.annatasha.validator.internal.structures.FieldInformation;
import com.google.code.annatasha.validator.internal.structures.MethodInformation;
import com.google.code.annatasha.validator.internal.structures.TypeInformation;

public class MethodBodyVerifier extends ASTVisitor {

	private final AnnatashaValidationResolver visitor;
	private final IResource resource;

	private int verifiers = 0;

	private boolean readAccessFlag;
	private boolean writeAccessFlag;
	private boolean threadStarterParameter;

	private CoreException exception = null;
	private MethodInformation method;

	public MethodBodyVerifier(AnnatashaValidationResolver visitor, IResource resource,
			MethodInformation method) {
		this.visitor = visitor;
		this.resource = resource;
		this.method = method;
	}

	public void buildAccessStructures(ASTNode node) throws CoreException {
		exception = null;
		node.accept(this);
		if (exception != null)
			throw exception;
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
		return false;
	}

	@Override
	public boolean visit(AssertStatement node) {
		verify(node.getExpression(), true, false, false);
		if (node.getMessage() != null) {
			verify(node.getExpression(), true, false, false);
		}
		return false;
	}

	@Override
	public boolean visit(Assignment node) {
		verify(node.getLeftHandSide(), node.getOperator() != Operator.ASSIGN,
				true, false);
		verify(node.getRightHandSide(), true, false, false);

		try {
			ITypeBinding lhs = node.getLeftHandSide().resolveTypeBinding();
			ITypeBinding rhs = node.getRightHandSide().resolveTypeBinding();
			validateAssignment(node, lhs, rhs);
		} catch (CircularReferenceException e) {
			// XXX error handler!!!
		} catch (CoreException e) {
			exception = e;
		}
		return false;
	}

	@Override
	public boolean visit(CastExpression node) {
		ITypeBinding src = node.getExpression().resolveTypeBinding();
		ITypeBinding dest = node.resolveTypeBinding();
		try {
			validateAssignment(node, dest, src);
		} catch (CircularReferenceException e) {
			// XXX error handler!!!
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(ClassInstanceCreation node) {
		try {
			MethodInformation methodInfo = visitor.getMethodInfo(node
					.resolveConstructorBinding());

			List<Expression> params = node.arguments();
			validateParameters(node.resolveConstructorBinding(), methodInfo,
					params);
		} catch (CircularReferenceException ex) {
			// XXX handle
		}
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		if (node.getInitializer() != null) {
			verify(node.getInitializer(), true, false, false);
			try {
				validateAssignment(node, node.resolveBinding().getType(), node
						.getInitializer().resolveTypeBinding());
			} catch (CircularReferenceException e) {
				// XXX handle error
			} catch (CoreException e) {
				exception = e;
			}
		}
		return true;
	}

	@Override
	public boolean visit(FieldAccess node) {
		verify(node.getExpression(), true, false, false);

		IVariableBinding binding = node.resolveFieldBinding();
		checkAccessPolicy(node, binding);
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		MethodInformation info;
		try {
			info = visitor.getMethodInfo(binding);

			if (node.getExpression() != null) {
				verify(node.getExpression(), true, false, false);
			}

			List<Expression> params = node.arguments();

			validateParameters(binding, info, params);
		} catch (CircularReferenceException e1) {
			// XXX
		}
		return false;
	}

	/**
	 * @param binding
	 * @param info
	 * @param params
	 * @throws CircularReferenceException
	 */
	private void validateParameters(IMethodBinding binding,
			MethodInformation info, List<Expression> params)
			throws CircularReferenceException {
		ArrayList<Integer> threadStarterParameters = info
				.getThreadStarterParameters();
		ITypeBinding[] paramsTypes = binding.getParameterTypes();

		int cursor = 0;
		int curValue = threadStarterParameters.size() > 0 ? threadStarterParameters
				.get(0)
				: -1;

		int len = params.size();
		for (int i = 0; i < len; ++i) {
			final ITypeBinding paramType = paramsTypes[i];
			final Expression paramValue = params.get(i);
			boolean write = paramType.getDimensions() != 0;
			verify(paramValue, true, write, i == curValue);
			if (i != curValue) {
				try {
					validateAssignment(paramValue, paramType, paramValue
							.resolveTypeBinding());
				} catch (CoreException e) {
					exception = e;
				}
			} else {
				++cursor;
				curValue = threadStarterParameters.size() > cursor ? threadStarterParameters
						.get(cursor)
						: -1;
			}
		}
	}

	@Override
	public boolean visit(QualifiedName node) {
		IBinding binding = node.resolveBinding();
		if (binding instanceof IVariableBinding) {
			IVariableBinding var = (IVariableBinding) binding;
			verify(node.getQualifier(), true, false, false);
			checkAccessPolicy(node, var);
		}
		return false;
	}

	@Override
	public boolean visit(SimpleName node) {
		IBinding binding = node.resolveBinding();
		if (binding instanceof IVariableBinding) {
			IVariableBinding var = (IVariableBinding) binding;
			checkAccessPolicy(node, var);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(SuperConstructorInvocation node) {
		try {
			MethodInformation methodInfo = visitor.getMethodInfo(node
					.resolveConstructorBinding());

			List<Expression> params = node.arguments();
			validateParameters(node.resolveConstructorBinding(), methodInfo,
					params);
		} catch (CircularReferenceException ex) {
			// XXX handle
		}
		return false;
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		IVariableBinding var = node.resolveFieldBinding();
		checkAccessPolicy(node, var);
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(SuperMethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		MethodInformation info;
		try {
			info = visitor.getMethodInfo(binding);
			validateParameters(binding, info, node.arguments());
		} catch (CircularReferenceException e) {
			// XXX handle
		}
		return false;
	}

	@Override
	public boolean visit(ThisExpression node) {
		return false;
	}

	@Override
	public boolean visit(PostfixExpression node) {
		verify(node.getOperand(), true, true, false);
		return true;
	}

	@Override
	public boolean visit(PrefixExpression node) {
		verify(node.getOperand(), true, true, false);
		return true;
	}

	private void verify(Expression node, boolean rFlag, boolean wFlag,
			boolean tsFlag) {
		boolean oldR = readAccessFlag;
		boolean oldW = writeAccessFlag;
		boolean oldTS = threadStarterParameter;
		readAccessFlag = rFlag;
		writeAccessFlag = wFlag;
		threadStarterParameter = tsFlag;
		++verifiers;
		try {
			node.accept(this);
		} finally {
			readAccessFlag = oldR;
			writeAccessFlag = oldW;
			threadStarterParameter = oldTS;
			--verifiers;
		}
	}

	/**
	 * @param node
	 *            TODO
	 * @param binding
	 */
	private void checkAccessPolicy(ASTNode node, IVariableBinding binding) {
		assert verifiers != 0;

		if (binding.isField()) {
			try {
				FieldInformation fieldInfo = visitor.getFieldInfo(binding);

				if (readAccessFlag) {
					if (!method.getExecPermissions().mightAccess(
							fieldInfo.getReadPermissions())) {
						visitor.reportError(resource, node,
								Error.MethodAttemptsToReadInaccessibleVariable);
					}
				}

				if (writeAccessFlag) {
					if (!method.getExecPermissions().mightAccess(
							fieldInfo.getWritePermissions())) {
						visitor
								.reportError(
										resource,
										node,
										Error.MethodAttemptsToWriteInaccessibleVariable);
					}
				}
			} catch (CircularReferenceException e) {
			} catch (CoreException e) {
				exception = e;
			}
		} else if (binding.isParameter()) {
			if (!threadStarterParameter && visitor.isThreadStarter(binding)) {
				try {
					visitor.reportError(resource, node,
							Error.MethodAttemptsToAccessThreadStarterParameter);
				} catch (CoreException e) {
					exception = e;
				}
			}
		}
	}

	private void validateAssignment(ASTNode node, ITypeBinding dest,
			ITypeBinding src) throws CircularReferenceException, CoreException {
		TypeInformation destInfo = visitor.getTypeInfo(dest);
		TypeInformation srcInfo = visitor.getTypeInfo(src);

		if (srcInfo.isThreadStarter()
				&& (!destInfo.isThreadStarter() || destInfo
						.getSuperThreadMarkers()[0] != srcInfo
						.getSuperThreadMarkers()[0])) {
			visitor.reportError(resource, node,
					AnnatashaValidationResolver.Error.InvalidTypeCast);
		}
	}

}
