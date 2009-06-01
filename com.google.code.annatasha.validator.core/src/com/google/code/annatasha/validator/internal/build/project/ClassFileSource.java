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

package com.google.code.annatasha.validator.internal.build.project;

import org.eclipse.jdt.core.IClassFile;

import com.google.code.annatasha.validator.internal.build.markers.IMarkerFactory;
import com.google.code.annatasha.validator.internal.build.markers.NullMarkerFactory;
import com.google.code.annatasha.validator.internal.build.symbols.SymbolInformation;

public final class ClassFileSource implements ISourcePolicy {
	
	private boolean init = false;
	private long stamp = 0;
	private final IClassFile file;
	
	public ClassFileSource(IClassFile file) {
		this.file = file;
	}

	public IMarkerFactory getMarkerFactory(SymbolInformation symbolInformation) {
		return new NullMarkerFactory();
	}

	public boolean hasChanged() {
		return !init || file.getResource().getModificationStamp() > stamp;
	}

	public void markSynchronised() {
		init = true;
		stamp = file.getResource().getModificationStamp();
	}

}
