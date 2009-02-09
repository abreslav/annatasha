package com.google.code.annatasha.validator.internal.build;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
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

public class AccessBuilder extends ASTVisitor {

	private final Set<IVariableBinding> readAccess;
	private final Set<IVariableBinding> writeAccess;
	private final Set<IMethodBinding> execAccess;
	
	private int verifiers = 0;

	private boolean readAccessFlag;
	private boolean writeAccessFlag;

	public AccessBuilder(final Set<IVariableBinding> readAccess,
			final Set<IVariableBinding> writeAccess,
			final Set<IMethodBinding> execAccess) {
		this.readAccess = readAccess;
		this.writeAccess = writeAccess;

		this.execAccess = execAccess;
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
		return false;
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
		verify(node.getInitializer(), true, false);
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
		verify(node.getExpression(), true, false);

		List<Expression> params = (List<Expression>) node
				.getStructuralProperty(MethodInvocation.ARGUMENTS_PROPERTY);
		for (Expression param : params) {
			verify(param, true, false);
		}

		IMethodBinding binding = node.resolveMethodBinding();
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
		verify(node.getExpression(), true, false);

		List<Expression> params = (List<Expression>) node
				.getStructuralProperty(MethodInvocation.ARGUMENTS_PROPERTY);
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

}
