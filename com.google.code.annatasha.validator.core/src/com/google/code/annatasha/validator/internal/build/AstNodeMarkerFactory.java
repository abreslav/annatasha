package com.google.code.annatasha.validator.internal.build;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.code.annatasha.validator.core.AnnatashaCore;

class AstNodeMarkerFactory implements IMarkerFactory {
	
	private final IResource resource;
	private final int startPosition;
	private final int endPosition;
	
	public AstNodeMarkerFactory(IResource resource, ASTNode node) {
		this.resource = resource;
		this.startPosition = node.getStartPosition();
		this.endPosition = node.getStartPosition() + node.getLength();
	}

	public IMarker createMarker(int code, int severity, String message) throws CoreException {
		IMarker marker = resource.createMarker(AnnatashaCore.MARKER_TYPE);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.SEVERITY, severity);
		marker.setAttribute(IMarker.CHAR_START, startPosition);
		marker.setAttribute(IMarker.CHAR_END, endPosition);
		return marker;
	}

}
