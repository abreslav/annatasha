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

/**
 * 
 */
package com.google.code.annatasha.validator.internal.build;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;

abstract class TaskNode {
	
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

	public abstract void acceptVisitor(ITaskVisitor visitor) throws CoreException;

}
