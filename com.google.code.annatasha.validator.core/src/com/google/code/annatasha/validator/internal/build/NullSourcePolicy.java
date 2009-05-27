package com.google.code.annatasha.validator.internal.build;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

final class NullSourcePolicy implements ISourcePolicy {

	private boolean synced = false;

	public void markSynchronised() {
		synced = true;
	}

	public boolean hasChanged() {
		return !synced;
	}

	public IMarkerFactory getMarkerFactory(SymbolInformation symbolInformation) {
		return new IMarkerFactory() {

			public IMarker createMarker(int code, int severity, String message)
					throws CoreException {
				return null;
			}
			
		};
	}

}
