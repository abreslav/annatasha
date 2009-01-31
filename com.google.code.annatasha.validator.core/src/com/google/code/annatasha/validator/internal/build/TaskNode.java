/**
 * 
 */
package com.google.code.annatasha.validator.internal.build;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;

public abstract class TaskNode {
	
	private final IResource resource;
	private final ASTNode node;
	private final IBinding binding;
	
	public TaskNode(IResource resource, ASTNode node, IBinding binding) {
		this.resource = resource;
		this.node = node;
		this.binding = binding;
	}
	
	public IResource getResource() {
		return resource;
	}
	
	public ASTNode getNode() {
		return node;
	}
	
	public IBinding getBinding() {
		return binding;
	}

	public abstract void acceptVisitor(ITaskVisitor visitor);

}
