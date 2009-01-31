package com.google.code.annatasha.validator.internal.build;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IVariableBinding;

public final class FieldTaskNode extends TaskNode {

	public FieldTaskNode(IResource resource,
			ASTNode node, IVariableBinding binding) {
		super(resource, node, binding);
	}
	
	public IVariableBinding getBinding() {
		return (IVariableBinding) super.getBinding();
	}
	
	@Override
	public void acceptVisitor(ITaskVisitor visitor) {
		visitor.visit(this);
	}
	

}
