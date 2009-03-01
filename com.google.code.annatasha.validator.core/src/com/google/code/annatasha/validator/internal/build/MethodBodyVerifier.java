package com.google.code.annatasha.validator.internal.build;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.Assignment.Operator;

import com.google.code.annatasha.validator.internal.structures.MethodInformation;
import com.google.code.annatasha.validator.internal.structures.TypeInformation;

public class MethodBodyVerifier extends ASTVisitor {

	private final ValidationVisitor visitor;
	private final IResource resource;

	private final Set<IVariableBinding> readAccess;
	private final Set<IVariableBinding> writeAccess;
	private final Set<IMethodBinding> execAccess;

	private int verifiers = 0;

	private boolean readAccessFlag;
	private boolean writeAccessFlag;
	
	private CoreException exception = null;

	public MethodBodyVerifier(ValidationVisitor visitor, IResource resource,
			final Set<IVariableBinding> readAccess,
			final Set<IVariableBinding> writeAccess,
			final Set<IMethodBinding> execAccess) {
		this.visitor = visitor;
		this.resource = resource;

		this.readAccess = readAccess;
		this.writeAccess = writeAccess;

		this.execAccess = execAccess;
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
		verify(node.getArray(), true, true);
		verify(node.getIndex(), true, false);
		return false;
	}

	@Override
	public boolean visit(AssertStatement node) {
		verify(node.getExpression(), true, false);
		if (node.getMessage() != null) {
			verify(node.getExpression(), true, false);
		}
		return false;
	}

	@Override
	public boolean visit(Assignment node) {
		verify(node.getLeftHandSide(), node.getOperator() != Operator.ASSIGN,
				true);
		verify(node.getRightHandSide(), true, false);

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
		List<Expression> params = (List<Expression>) node
				.getStructuralProperty(ClassInstanceCreation.ARGUMENTS_PROPERTY);
		if (params != null) {
			for (Expression param : params) {
				verify(param, true, false);
			}
		}
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		if (node.getInitializer() != null) {
			verify(node.getInitializer(), true, false);
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldAccess node) {
		verify(node.getExpression(), true, false);

		IVariableBinding binding = node.resolveFieldBinding();
		pushAccessPolicy(binding);
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MethodInvocation node) {
		IMethodBinding binding = node.resolveMethodBinding();
		MethodInformation info = visitor.getMethodInfo(binding);
		// boolean[] threadStarters = info.getThreadStarterFlags();

		if (node.getExpression() != null) {
			verify(node.getExpression(), true, false);
		}

		List<Expression> params = (List<Expression>) node
				.getStructuralProperty(MethodInvocation.ARGUMENTS_PROPERTY);
		List<ITypeBinding> paramsTypes = (List<ITypeBinding>) node
				.getStructuralProperty(MethodInvocation.TYPE_ARGUMENTS_PROPERTY);
		Iterator<ITypeBinding> paramTypeIterator = paramsTypes.iterator();
		int i = 0;
		for (Expression param : params) {
			// XXX
			// ITypeBinding paramType = paramTypeIterator.next();
			// verify(param, true, false);
			// if (!threadStarters[i++]) {
			// validateAssignment(param, paramType, param
			// .resolveTypeBinding());
			// }
		}

		execAccess.add(binding);
		return false;
	}

	@Override
	public boolean visit(QualifiedName node) {
		IBinding binding = node.resolveBinding();
		if (binding instanceof IVariableBinding) {
			IVariableBinding var = (IVariableBinding) binding;
			verify(node.getQualifier(), true, false);
			pushAccessPolicy(var);
		}
		return false;
	}

	@Override
	public boolean visit(SimpleName node) {
		IBinding binding = node.resolveBinding();
		if (binding instanceof IVariableBinding) {
			IVariableBinding var = (IVariableBinding) binding;
			pushAccessPolicy(var);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(SuperConstructorInvocation node) {
//		verify(node.getExpression(), true, false);

		List<Expression> params = (List<Expression>) node
				.getStructuralProperty(SuperConstructorInvocation.ARGUMENTS_PROPERTY);
		for (Expression param : params) {
			verify(param, true, false);
		}
		return false;
	}

	@Override
	public boolean visit(SuperFieldAccess node) {
		IVariableBinding var = node.resolveFieldBinding();
		pushAccessPolicy(var);
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(SuperMethodInvocation node) {
		List<Expression> params = (List<Expression>) node
				.getStructuralProperty(MethodInvocation.ARGUMENTS_PROPERTY);
		for (Expression param : params) {
			verify(param, true, false);
		}

		execAccess.add(node.resolveMethodBinding());
		return false;
	}

	@Override
	public boolean visit(ThisExpression node) {
		return false;
	}

	private void verify(Expression node, boolean rFlag, boolean wFlag) {
		boolean oldR = readAccessFlag;
		boolean oldW = writeAccessFlag;
		readAccessFlag = rFlag;
		writeAccessFlag = wFlag;
		++verifiers;
		try {
			node.accept(this);
		} finally {
			readAccessFlag = oldR;
			writeAccessFlag = oldW;
			--verifiers;
		}
	}

	/**
	 * @param binding
	 */
	private void pushAccessPolicy(IVariableBinding binding) {
		assert verifiers != 0;

		if (binding.isField()) {
			if (readAccessFlag)
				readAccess.add(binding);
			if (writeAccessFlag)
				writeAccess.add(binding);
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
					ValidationVisitor.Error.InvalidTypeCast);
		}
	}

}
