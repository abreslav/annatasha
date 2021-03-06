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
import com.google.code.annatasha.validator.internal.build.project.ISourcePolicy;


public abstract class SymbolInformation {

//	private final IAnnatashaModelResolver resolver;
	private final String key;
	private ISourcePolicy sourcePolicy;
	
	public abstract void acceptVisitor(SymbolVisitor visitor);

//	private final HashSet<String> dependent = new HashSet<String>();

	public SymbolInformation(IModelResolver resolver,
			final String key, ISourcePolicy sourcePolicy) {
		if (key == null)
			throw new IllegalArgumentException("key cannot be null");

//		this.resolver = resolver;
		this.key = key;
		this.sourcePolicy = sourcePolicy;
	}

	public String getKey() {
		return key;
	}

	public ISourcePolicy getSourcePolicy() {
		return sourcePolicy;
	}

//	public void setDependencyOn(Iterable<? extends String> symbols) {
//		for (String symbol : symbols) {
//			SymbolInformation symbolInformation = resolver
//					.getSymbolInformation(symbol);
//			if (symbolInformation != null) {
//				symbolInformation.dependent.add(this.key);
//			}
//		}
//	}
//
//	public void removeDependencyFrom(Iterable<? extends String> symbols) {
//		for (String symbol : symbols) {
//			SymbolInformation symbolInformation = resolver
//					.getSymbolInformation(symbol);
//			if (symbolInformation != null) {
//				symbolInformation.dependent.remove(this.key);
//			}
//		}
//	}
//
//	public Set<String> getDependent() {
//		return dependent;
//	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj == null)
			return false;

		if (obj.getClass().equals(this.getClass())) {
			SymbolInformation other = (SymbolInformation) obj;
			return this.key.equals(other.key);
		}
		return false;
	}

}
