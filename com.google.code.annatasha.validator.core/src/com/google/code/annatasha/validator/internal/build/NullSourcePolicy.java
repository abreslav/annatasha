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
