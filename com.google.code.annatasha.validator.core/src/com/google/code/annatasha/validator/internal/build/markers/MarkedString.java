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


public final class MarkedString implements IMarkedValue {

	private final IMarkerFactory factory;
	public final String value;

	public MarkedString(IMarkerFactory factory, String value) {
		this.factory = factory;
		this.value = value;
	}

	public IMarkerFactory getMarkerFactory() {
		return factory;
	}
	
}
