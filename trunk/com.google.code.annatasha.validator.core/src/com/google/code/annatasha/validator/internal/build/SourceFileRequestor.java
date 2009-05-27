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

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public final class SourceFileRequestor extends ASTRequestor {

	private final ISourceFileRequestorCallback callback;

	public SourceFileRequestor(ISourceFileRequestorCallback callback) {
		this.callback = callback;
	}

	@Override
	public void acceptAST(ICompilationUnit source, CompilationUnit ast) {
		callback.sourceAccepted(source);

		SourceFileProcessor visitor = new SourceFileProcessor(source
				.getResource(), callback);
		ast.accept(visitor);
		
		callback.sourceUpdated(source);
		super.acceptAST(source, ast);
	}

}