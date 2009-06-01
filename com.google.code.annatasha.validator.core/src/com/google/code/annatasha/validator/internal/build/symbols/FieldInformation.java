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

package com.google.code.annatasha.validator.internal.build.symbols;

import com.google.code.annatasha.validator.internal.build.IModelResolver;
import com.google.code.annatasha.validator.internal.build.markers.MarkedBoolean;
import com.google.code.annatasha.validator.internal.build.markers.MarkedString;
import com.google.code.annatasha.validator.internal.build.project.ISourcePolicy;

public final class FieldInformation extends SymbolInformation {

	// Obtained during local lookup
	public MarkedString type;

	public Permissions readPermissions;
	public Permissions writePermissions;

	public MarkedBoolean threadStarter;

	// Obtained during global lookup
	// NONE

	public FieldInformation(IModelResolver resolver, String key,
			ISourcePolicy sourcePolicy) {
		super(resolver, key, sourcePolicy);
	}

	public MarkedString getType() {
		return type;
	}

	public Permissions getReadPermissions() {
		return readPermissions;
	}

	public Permissions getWritePermissions() {
		return writePermissions;
	}

	public boolean isThreadStarter() {
		return threadStarter.value;
	}

	@Override
	public void acceptVisitor(SymbolVisitor visitor) {
		visitor.visit(this);
	}

}
