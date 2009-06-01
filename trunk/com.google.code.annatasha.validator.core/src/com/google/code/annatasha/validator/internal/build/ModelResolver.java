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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.code.annatasha.validator.internal.build.symbols.FieldInformation;
import com.google.code.annatasha.validator.internal.build.symbols.MethodInformation;
import com.google.code.annatasha.validator.internal.build.symbols.SymbolInformation;
import com.google.code.annatasha.validator.internal.build.symbols.TypeInformation;

class ModelResolver implements IModelResolver {
	
	private Map<String, SymbolInformation> symbols;

	public ModelResolver() {
		symbols = new HashMap<String, SymbolInformation>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #getTypeInformation(java.lang.String)
	 */
	public TypeInformation getTypeInformation(final String key) {
		try {
			return (TypeInformation) symbols.get(key);
		} catch (ClassCastException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #setTypeInformation(java.lang.String,
	 * com.google.code.annatasha.validator.internal.structures.TypeInformation)
	 */
	public void setTypeInformation(final String key,
			final TypeInformation typeInformation) {
		symbols.put(key, typeInformation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #setFieldInforamtion(java.lang.String,
	 * com.google.code.annatasha.validator.internal.structures.FieldInformation)
	 */
	public void setFieldInformation(String key, FieldInformation result) {
		symbols.put(key, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #getFieldInformation(java.lang.String)
	 */
	public FieldInformation getFieldInformation(String key) {
		try {
			return (FieldInformation) symbols.get(key);
		} catch (ClassCastException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #getMethodInformation(java.lang.String)
	 */
	public MethodInformation getMethodInformation(String key) {
		try {
			return (MethodInformation) symbols.get(key);
		} catch (ClassCastException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #setMethodInformation(java.lang.String,
	 * com.google.code.annatasha.validator
	 * .internal.structures.MethodInformation)
	 */
	public void setMethodInformation(String key, MethodInformation result) {
		symbols.put(key, result);
	}

	public void removeInformation(String key) {
		symbols.remove(key);
	}

	public SymbolInformation getSymbolInformation(String key) {
		return symbols.get(key);
	}

	public void removeSymbolInformation(String key) {
		symbols.remove(key);
	}

	public Iterator<SymbolInformation> iterator() {
		return symbols.values().iterator();
	}

	public Collection<String> getKeys() {
		return symbols.keySet();
	}

	public void clear() {
		symbols.clear();
	}

}