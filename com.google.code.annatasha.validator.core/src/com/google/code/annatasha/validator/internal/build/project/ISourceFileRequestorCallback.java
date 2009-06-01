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
package com.google.code.annatasha.validator.internal.build.project;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;

import com.google.code.annatasha.validator.internal.build.IProblemsReportFactory;
import com.google.code.annatasha.validator.internal.build.symbols.FieldInformation;
import com.google.code.annatasha.validator.internal.build.symbols.MethodInformation;
import com.google.code.annatasha.validator.internal.build.symbols.TypeInformation;

public interface ISourceFileRequestorCallback extends IProblemsReportFactory {

	public void symbolRequested(IBinding binding);

	public IBinding getBinding(String key);

	public void symbolDefined(String key);

	public boolean isDefined(String key);

	public String[] getUndefined();

	/**
	 * If type information exists in validation resolver for specific type
	 * binding then returns it. Else returns the newly allocated type
	 * information object with key and sourcePolicy appropriately setup.
	 * 
	 * @param binding
	 * @return
	 */
	public TypeInformation getTypeInformation(String key);

	/**
	 * If method information exists in validation resolver for specific method
	 * binding then returns it. Else returns the newly allocated method
	 * information object with key and sourcePolicy appropriately setup.
	 * 
	 * @param binding
	 * @return
	 */
	public MethodInformation getMethodInformation(String key);

	/**
	 * If field information exists in validation resolver for specific field
	 * binding then returns it. Else returns the newly allocated field
	 * information object with key and sourcePolicy appropriately setup.
	 * 
	 * @param binding
	 * @return
	 */
	public FieldInformation getFieldInformation(String key);

	/**
	 * Enqueues the symbol for deep validation
	 * 
	 * @param symbols
	 */
	// public void enqueueRevalidation(Iterable<String> symbols);
	/**
	 * Called before re-parsing AST of compilation unit
	 * 
	 * @param source
	 */
	public void sourceAccepted(ICompilationUnit source);

	/**
	 * Called after all structures in compilation unit were re-parsed
	 * 
	 * @param source
	 */
	public void sourceUpdated(ICompilationUnit source);

	public void enqueueMethodBodyValidation(IResource resource,
			IMethodBinding binding, ASTNode body);

}