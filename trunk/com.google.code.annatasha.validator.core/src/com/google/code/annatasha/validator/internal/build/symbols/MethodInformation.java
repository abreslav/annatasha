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
package com.google.code.annatasha.validator.internal.build.symbols;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.IMethodBinding;

import com.google.code.annatasha.validator.internal.build.IModelResolver;
import com.google.code.annatasha.validator.internal.build.IExecPermissionsHost;
import com.google.code.annatasha.validator.internal.build.markers.MarkedString;
import com.google.code.annatasha.validator.internal.build.project.ISourcePolicy;

/**
 * @author Ivan Egorov
 * 
 */
public final class MethodInformation extends SymbolInformation implements
		IExecPermissionsHost {

	// Obtained during local lookup
	public IMethodBinding binding;

	public MarkedString name;
	public String declaringType;
	public boolean isStatic;
	public boolean isConstructor;

	public MarkedString returnType;
	public Permissions execPermissions;
	// The set of keys of thread starter parameters
	public ArrayList<Integer> threadStarters = new ArrayList<Integer>();
	public boolean entryPoint;
	public HashSet<String> referencedBindings;

	// Obtained during global lookup
	public MarkedString superDefinition;
	public ArrayList<MarkedString> superDeclarations = new ArrayList<MarkedString>();

	public boolean inheritedFromEntryPoint;

	public MethodInformation(IModelResolver resolver, String key,
			ISourcePolicy sourcePolicy) {
		super(resolver, key, sourcePolicy);
	}

	// public MethodInformation(
	// String key,
	// ISourcePolicy sourcePolicy,
	// MarkedString returnType,
	// Permissions execPermissions,
	// boolean entryPoint,
	// ArrayList<Integer> threadStarters) {
	// super(key, sourcePolicy);
	// this.returnType = returnType;
	// this.execPermissions = execPermissions;
	// this.entryPoint = entryPoint;
	// this.threadStarters = threadStarters;
	// }

	public MarkedString getName() {
		return name;
	}

	public MarkedString getReturnType() {
		return returnType;
	}

	public Permissions getExecPermissions() {
		return execPermissions;
	}

	public boolean hasSuperDeclaration() {
		return superDefinition != null || superDeclarations.size() != 0;
	}

	// New interface
	public boolean isEntryPoint() {
		return entryPoint;
	}

	public boolean isInheritedFromEntryPoint() {
		return inheritedFromEntryPoint;
	}

	@Override
	public void acceptVisitor(SymbolVisitor visitor) {
		visitor.visit(this);
	}

}
