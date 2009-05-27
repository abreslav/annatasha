/**
 * 
 */
package com.google.code.annatasha.validator.internal.build;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.google.code.annatasha.validator.core.AnnatashaCore;

final class SourceFileInformation implements ISourcePolicy {

	private final IFile resource;
	private long lastSynchronisedModification = IResource.NULL_STAMP;
	final Set<String> symbols = new HashSet<String>();

	public SourceFileInformation(IFile resource) {
		this.resource = resource;
	}

	public void markSynchronised() {
		lastSynchronisedModification = resource.getModificationStamp();
	}

	public boolean hasChanged() {
		return lastSynchronisedModification == IResource.NULL_STAMP
				|| resource.getModificationStamp() > lastSynchronisedModification;
	}

	public IMarkerFactory getMarkerFactory(SymbolInformation symbolInformation) {
		return new IMarkerFactory() {

			public IMarker createMarker(int code, int severity, String message)
					throws CoreException {
				IMarker marker = resource.createMarker(AnnatashaCore.MARKER_TYPE);
				marker.setAttribute(IMarker.MESSAGE, message);
				marker.setAttribute(IMarker.SEVERITY, severity);
				return marker;
			}
			
		};
	}

}