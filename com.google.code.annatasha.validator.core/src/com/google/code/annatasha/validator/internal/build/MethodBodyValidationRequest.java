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
