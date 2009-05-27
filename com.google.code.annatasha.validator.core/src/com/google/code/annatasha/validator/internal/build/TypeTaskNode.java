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
