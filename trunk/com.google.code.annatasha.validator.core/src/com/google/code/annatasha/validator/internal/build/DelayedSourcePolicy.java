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

final class DelayedSourcePolicy implements ISourcePolicy {

	private ISourcePolicy destination;

	public void markSynchronised() {
		if (destination != null) {
			destination.markSynchronised();
		}
	}

	public boolean hasChanged() {
		if (destination != null) {
			return destination.hasChanged();
		}
		return false;
	}

	void setSourcePolicy(ISourcePolicy destination) {
		// if (this.destination == null) {
		this.destination = destination;
		// } else {
		// throw new UnsupportedOperationException(
		// "destination is assignable only once");
		// }
	}

	boolean isAssigned() {
		return destination != null;
	}

	public IMarkerFactory getMarkerFactory(SymbolInformation symbolInformation) {
		assert destination != null;
		return destination.getMarkerFactory(symbolInformation);
	}

}
