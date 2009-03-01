package com.google.code.annatasha.validator.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import com.google.code.annatasha.validator.internal.build.SourceFileRequestor;

public class AnnatashaBuilder extends IncrementalProjectBuilder {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("unchecked")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		}/*
		 * else { IResourceDelta delta = getDelta(getProject()); if (delta ==
		 * null) { fullBuild(monitor); } else { incrementalBuild(delta,
		 * monitor); } }
		 */
		return null;
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {

			final IProject prj = getProject(); // .accept(new
			// SampleResourceVisitor());
			final IJavaProject project = JavaCore.create(prj);

			final ArrayList<ICompilationUnit> unitsList = new ArrayList<ICompilationUnit>();
			final ArrayList<IClassFile> classesList = new ArrayList<IClassFile>();

			for (IPackageFragment fragment : project.getPackageFragments()) {
				unitsList.addAll(Arrays.asList(fragment.getCompilationUnits()));
				classesList.addAll(Arrays.asList(fragment.getClassFiles()));
			}

			ICompilationUnit[] units = new ICompilationUnit[unitsList.size()];
			units = unitsList.toArray(units);

			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setResolveBindings(true);
			parser.setBindingsRecovery(true);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setProject(project);

			SourceFileRequestor requestor = new SourceFileRequestor();
			parser.createASTs(units, new String[0], requestor, monitor);
			requestor.validate();
			System.out.println("Built succesfully");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
