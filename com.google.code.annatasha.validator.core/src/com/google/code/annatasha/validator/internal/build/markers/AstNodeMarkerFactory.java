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

package com.google.code.annatasha.validator.internal.build.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.code.annatasha.validator.core.AnnatashaCore;

public class AstNodeMarkerFactory implements IMarkerFactory {
	
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
