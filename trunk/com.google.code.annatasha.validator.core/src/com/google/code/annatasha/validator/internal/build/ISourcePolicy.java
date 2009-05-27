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

interface ISourcePolicy {

	/**
	 * true if source has been updated since last synchronisation operation
	 * 
	 * @return
	 */
	boolean hasChanged();

	/**
	 * Sets new synchronisation point, flushes {@link #hasChanged()} flag
	 */
	void markSynchronised();

	/**
	 * Returns marker factory for specified symbol.
	 * 
	 * @param symbolInformation
	 *            symbol which has this object as source policy
	 */
	IMarkerFactory getMarkerFactory(SymbolInformation symbolInformation);

}
