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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClassFile;

import com.google.code.annatasha.validator.core.AnnatashaCore;

final class ClassFileMarkerFactory implements IMarkerFactory {

	private final IClassFile file;

	public ClassFileMarkerFactory(IClassFile file) {
		this.file = file;
	}

	public IMarker createMarker(int code, int severity, String message)
			throws CoreException {
		IMarker marker = file.getResource().createMarker(
				AnnatashaCore.MARKER_TYPE);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.SEVERITY, severity);
		return marker;
	}

}
