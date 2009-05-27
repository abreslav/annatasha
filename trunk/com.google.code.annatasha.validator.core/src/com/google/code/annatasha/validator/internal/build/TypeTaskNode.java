package com.google.code.annatasha.validator.internal.build;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;


/**
 * TypeInformation full-filling
 * 
 * @author ivan
 * 
 */
final class TypeTaskNode extends TaskNode {

	public TypeTaskNode(IResource resource, ASTNode node, ITypeBinding binding) {
		super(resource, node, binding);
	}
	
	public ITypeBinding getBinding() {
		return (ITypeBinding) super.getBinding();
	}

	@Override
	public void acceptVisitor(ITaskVisitor visitor) throws CoreException {
		visitor.visit(this);
	}
	
	
	
}
