package com.google.code.annatasha.validator.core;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.CategorizedProblem;

public class CompilationParticipant extends
		org.eclipse.jdt.core.compiler.CompilationParticipant {

	// private static volatile int counter = 0;

	private final HashSet<IJavaProject> inputProjects = new HashSet<IJavaProject>();

	public CompilationParticipant(Collection<IJavaProject> projects) {
		inputProjects.addAll(projects);
		// TODO Do we need collect dependent projects?
	}

	@Override
	public boolean isActive(IJavaProject project) {
		return true;
	}

	@Override
	public boolean isAnnotationProcessor() {
		return true;
	}

	@Override
	public void buildStarting(BuildContext[] files, boolean isBatch) {
		if (!isBatch)
			return;
		
//		for (BuildContext ctx : files) {
//			// IFile file = ctx.getFile();
//			// ctx
//			// .recordNewProblems(new CategorizedProblem[] { new
//			// VerificationProblem(
//			// CategorizedProblem.CAT_UNSPECIFIED, file) });
//		}
	}
	
	@Override
	public void buildFinished(IJavaProject project) {
		int left;
		synchronized (inputProjects) {
			inputProjects.remove(project);
			left = inputProjects.size();
		}
		
		if (left == 0) {
			startAnalysis();
		}
	}

	@Override
	public void processAnnotations(BuildContext[] files) {
		IProject prj = null;
		for (BuildContext ctx : files) {
			IProject p = ctx.getFile().getProject();
			if (prj == null)
				prj = p;
			if (p != prj)
				ctx
						.recordNewProblems(new CategorizedProblem[] { new VerificationProblem(
								CategorizedProblem.CAT_UNSPECIFIED, ctx
										.getFile()) });
		}
	}

	private void startAnalysis() {
		// TODO Auto-generated method stub
		
	}
	
}
