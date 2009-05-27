package com.google.code.annatasha.validator.internal.build;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

final class NullMarkerFactory implements IMarkerFactory {

	public IMarker createMarker(int code, int severity, String message)
			throws CoreException {
		return null;
	}

}
