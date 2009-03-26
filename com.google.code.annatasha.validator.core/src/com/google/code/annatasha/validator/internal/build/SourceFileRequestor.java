package com.google.code.annatasha.validator.internal.build;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;

import com.google.code.annatasha.validator.core.AnnatashaCore;
import com.google.code.annatasha.validator.internal.build.tasks.TaskNode;

public final class SourceFileRequestor extends ASTRequestor {

	private final Map<IBinding, TaskNode> bindings = new HashMap<IBinding, TaskNode>();
	private final Set<TaskNode> tasks = new HashSet<TaskNode>();

	@Override
	public void acceptAST(ICompilationUnit source, CompilationUnit ast) {
		System.out.println("Accepting source: " + source.getElementName());
		try {
			source.getResource().deleteMarkers(AnnatashaCore.MARKER_TYPE, true,
					IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			// XXX Propagate it somewhere!!!
		}
		SourceFileLightProcessor visitor = new SourceFileLightProcessor(source.getResource(),
				tasks);
		ast.accept(visitor);
		super.acceptAST(source, ast);
	}

	public void validate() throws CoreException {
		for (TaskNode task : tasks) {
			bindings.put(task.getBinding(), task);
		}

		AnnatashaValidationResolver visitor = new AnnatashaValidationResolver(bindings);
		for (TaskNode task : tasks) {
			task.acceptVisitor(visitor);
		}
	}
}