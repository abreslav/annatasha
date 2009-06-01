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

import java.util.HashMap;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;

import com.google.code.annatasha.validator.core.AnnatashaCore;
import com.google.code.annatasha.validator.core.AnnatashaProject;
import com.google.code.annatasha.validator.internal.build.markers.IMarkerFactory;

public final class AnnatashaModel implements IReportListener {

	private HashMap<IJavaProject, AnnatashaProject> projects = new HashMap<IJavaProject, AnnatashaProject>();
	private IModelResolver resolver = new ModelResolver();
	private final ModelValidator validator = new ModelValidator(this);

	public AnnatashaProject getProject(IJavaProject javaProject) {
		return projects.get(javaProject);
	}

	public void ensureProject(IJavaProject javaProject) throws CoreException {
		AnnatashaProject project = projects.get(javaProject);
		if (project == null) {
			projects.put(javaProject, (AnnatashaProject) javaProject
					.getProject().getNature(AnnatashaCore.NATURE_ID));
		}
	}

	public void addProject(IJavaProject project) throws CoreException {
		IProjectDescription description = project.getProject().getDescription();
		String[] natures = description.getNatureIds();

		for (int i = 0; i < natures.length; ++i) {
			if (AnnatashaCore.NATURE_ID.equals(natures[i])) {
				// Found the nature!
				return;
			}
		}

		// Not found. Add it.
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = AnnatashaCore.NATURE_ID;
		description.setNatureIds(newNatures);
		project.getProject().setDescription(description, null);

		projects.put(project, (AnnatashaProject) project.getProject()
				.getNature(AnnatashaCore.NATURE_ID));
		// sync(null);
	}

	public void removeProject(IJavaProject project) throws CoreException {
		IProjectDescription description = project.getProject().getDescription();
		String[] natures = description.getNatureIds();

		for (int i = 0; i < natures.length; ++i) {
			if (AnnatashaCore.NATURE_ID.equals(natures[i])) {
				// Remove the nature
				String[] newNatures = new String[natures.length - 1];
				System.arraycopy(natures, 0, newNatures, 0, i);
				System.arraycopy(natures, i + 1, newNatures, i, natures.length
						- i - 1);
				description.setNatureIds(newNatures);
				project.getProject().setDescription(description, null);
				project.getProject().deleteMarkers(AnnatashaCore.MARKER_TYPE,
						true, IResource.DEPTH_INFINITE);

				projects.remove(project);
				return;
			}
		}
	}

	public boolean isProject(IJavaProject project) {
		IProjectDescription description;
		try {
			description = project.getProject().getDescription();
		} catch (CoreException e) {
			return false;
		}
		String[] natures = description.getNatureIds();

		for (int i = 0; i < natures.length; ++i) {
			if (AnnatashaCore.NATURE_ID.equals(natures[i])) {
				return true;
			}
		}
		return false;
	}

	public void reportProblem(IMarkerFactory markerFactory, Error error) {
		try {
			markerFactory.createMarker(error.code, IMarker.SEVERITY_ERROR,
					error.message);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clean() {
		for (AnnatashaProject project : projects.values()) {
			try {
				project.clear();
				project.getProject().deleteMarkers(AnnatashaCore.MARKER_TYPE,
						true, IResource.DEPTH_INFINITE);
			} catch (CoreException e) {
			}
		}
		this.getResolver().clear();
		this.getValidator().clear();
	}

	public IModelResolver getResolver() {
		return resolver;
	}

	public ModelValidator getValidator() {
		return validator;
	}

}
