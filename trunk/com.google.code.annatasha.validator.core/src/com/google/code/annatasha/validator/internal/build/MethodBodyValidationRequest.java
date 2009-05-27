package com.google.code.annatasha.validator.internal.build;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;

final class MethodBodyValidationRequest {
	
	public final IResource resource;
	public final IMethodBinding binding;
	public final ASTNode body;

	public MethodBodyValidationRequest(IResource resource, IMethodBinding binding, ASTNode body) {
		this.resource = resource;
		this.binding = binding;
		this.body = body;
	}

}
