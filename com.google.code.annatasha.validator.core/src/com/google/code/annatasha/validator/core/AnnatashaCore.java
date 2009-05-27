package com.google.code.annatasha.validator.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

import com.google.code.annatasha.validator.internal.build.AnnatashaModel;

public final class AnnatashaCore {

	/**
	 * ID of this project nature
	 */
	public static final String NATURE_ID = "com.google.code.annatasha.validator.core.nature";
	public static final String BUILDER_ID = "com.google.code.annatasha.validator.core.builder";
	public static final String MARKER_TYPE = "com.google.code.annatasha.validator.core.problem";

	private final static AnnatashaModel model = new AnnatashaModel();

	private AnnatashaCore() {
	}

	// /**
	// * Toggles sample nature on a project
	// *
	// * @param project
	// * to have sample nature added or removed
	// */
	// public static void toggleNature(IJavaProject project) {
	// try {
	// IProjectDescription description = project.getProject().getDescription();
	// String[] natures = description.getNatureIds();
	//
	// for (int i = 0; i < natures.length; ++i) {
	// if (AnnatashaCore.NATURE_ID.equals(natures[i])) {
	// // Remove the nature
	// String[] newNatures = new String[natures.length - 1];
	// System.arraycopy(natures, 0, newNatures, 0, i);
	// System.arraycopy(natures, i + 1, newNatures, i,
	// natures.length - i - 1);
	// description.setNatureIds(newNatures);
	// project.getProject().setDescription(description, null);
	// return;
	// }
	// }
	//
	// // Add the nature
	// String[] newNatures = new String[natures.length + 1];
	// System.arraycopy(natures, 0, newNatures, 0, natures.length);
	// newNatures[natures.length] = AnnatashaCore.NATURE_ID;
	// description.setNatureIds(newNatures);
	// project.getProject().setDescription(description, null);
	// } catch (CoreException e) {
	// }
	// }

	public static AnnatashaModel getModel() {
		return model;
	}

	public static void addProject(IJavaProject project) throws CoreException {
		getModel().addProject(project);
	}
	
	public static void removeProject(IJavaProject project) throws CoreException {
		getModel().removeProject(project);
	}

	public static boolean isProject(IJavaProject project) {
		return getModel().isProject(project);
	}

}
