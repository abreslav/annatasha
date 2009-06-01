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

/**
 * 
 */
package com.google.code.annatasha.validator.internal.build.project;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.google.code.annatasha.validator.core.AnnatashaCore;
import com.google.code.annatasha.validator.internal.build.markers.IMarkerFactory;
import com.google.code.annatasha.validator.internal.build.symbols.SymbolInformation;

public final class SourceFilePolicy implements ISourcePolicy {

	private final IFile resource;
	private long lastSynchronisedModification = IResource.NULL_STAMP;
	public final Set<String> symbols = new HashSet<String>();

	public SourceFilePolicy(IFile resource) {
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