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