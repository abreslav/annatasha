package com.google.code.annatasha.validator.internal.build.tasks;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;


public final class MethodTaskNode extends TaskNode {

	public MethodTaskNode(IResource resource, ASTNode node, IMethodBinding binding) {
		super(resource, node, binding);
	}
	
	public IMethodBinding getBinding() {
		return (IMethodBinding) super.getBinding();
	}

	@Override
	public void acceptVisitor(ITaskVisitor visitor) throws CoreException {
		visitor.visit(this);
	}


}
