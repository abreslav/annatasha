package com.google.code.annatasha.validator.internal.build;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

interface IMarkerFactory {
	
	public abstract IMarker createMarker(int code, int severity, String message) throws CoreException;

}
