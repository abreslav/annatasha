package com.google.code.annatasha.validator.internal.build;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.google.code.annatasha.validator.core.AnnatashaCore;

final class ProjectMarkerFactory implements IMarkerFactory {

	private String name;
	private IProject project;

	public ProjectMarkerFactory(IProject project, String name) {
		this.name = name;
		this.project = project;
	}

	public IMarker createMarker(int code, int severity, String message)
			throws CoreException {
		IMarker marker = project.createMarker(AnnatashaCore.MARKER_TYPE);
		marker.setAttribute(IMarker.MESSAGE, message + " (" + name + ")");
		marker.setAttribute(IMarker.SEVERITY, severity);
		return marker;
	}

}
