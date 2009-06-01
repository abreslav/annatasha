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

import java.util.Collection;

import com.google.code.annatasha.validator.internal.build.symbols.FieldInformation;
import com.google.code.annatasha.validator.internal.build.symbols.MethodInformation;
import com.google.code.annatasha.validator.internal.build.symbols.SymbolInformation;
import com.google.code.annatasha.validator.internal.build.symbols.TypeInformation;

public interface IModelResolver extends Iterable<SymbolInformation> {

	public void setTypeInformation(String key, TypeInformation typeInformation);

	public TypeInformation getTypeInformation(String key);

	public void setFieldInformation(String key, FieldInformation result);

	public FieldInformation getFieldInformation(String key);

	public void setMethodInformation(String key, MethodInformation result);

	public MethodInformation getMethodInformation(String key);

	public SymbolInformation getSymbolInformation(String symbol);

	public void removeSymbolInformation(String key);

	public Collection<String> getKeys();

	public void clear();

}