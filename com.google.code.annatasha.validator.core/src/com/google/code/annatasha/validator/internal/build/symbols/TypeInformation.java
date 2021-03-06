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

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.dom.ITypeBinding;

import com.google.code.annatasha.validator.internal.build.IModelResolver;
import com.google.code.annatasha.validator.internal.build.IExecPermissionsHost;
import com.google.code.annatasha.validator.internal.build.markers.MarkedBoolean;
import com.google.code.annatasha.validator.internal.build.markers.MarkedString;
import com.google.code.annatasha.validator.internal.build.project.ISourcePolicy;
import com.google.code.annatasha.validator.internal.build.project.NullSourcePolicy;

public final class TypeInformation extends SymbolInformation implements
		IExecPermissionsHost {

	// public final static TypeInformation Anonymous = new TypeInformation();
	private final static HashMap<IModelResolver, TypeInformation> ATasks = new HashMap<IModelResolver, TypeInformation>();

	// ATask
	private final boolean anonymous;

	// Obtained during local lookup
	public ITypeBinding binding;
	public MarkedString name;
	public boolean clazz;
	public boolean iface;
	public boolean annot;
	public boolean entryPoint;
	public MarkedBoolean threadMarker;
	public MarkedString superClass;
	public ArrayList<MarkedString> superInterfaces;
	public Permissions execPermissions;
	public boolean hasMethods;

	// Obtained during global lookup
	public boolean threadStarter;
	public ArrayList<MarkedString> superThreadMarkers;
	public boolean inheritedFromEntryPoint;

	public static TypeInformation getATask(IModelResolver resolver) {
		TypeInformation result = ATasks.get(resolver);
		if (result == null) {
			result = new TypeInformation(resolver);
			ATasks.put(resolver, result);
		}
		return result;
	}

	private TypeInformation(IModelResolver resolver) {
		super(resolver, "<Annatasha>.ATask", new NullSourcePolicy());
		this.anonymous = true;
	}

	public TypeInformation(IModelResolver resolver, String key,
			ISourcePolicy sourcePolicy) {
		super(resolver, key, sourcePolicy);
		this.anonymous = false;
	}

	// public TypeInformation(IAnnatashaModelResolver resolver, String key,
	// ISourcePolicy sourcePolicy, ITypeBinding binding, boolean isClass,
	// boolean isInterface, boolean isAnnotation, boolean isThreadMarker,
	// boolean isThreadStarter, boolean isEntryPoint,
	// MarkedString superClass, MarkedString[] superInterfaces,
	// Permissions execPermissions) {
	// super(resolver, key, sourcePolicy);
	// this.binding = binding;
	// this.clazz = isClass;
	// this.iface = isInterface;
	// this.annot = isAnnotation;
	// this.threadMarker = isThreadMarker;
	// this.threadStarter = isThreadStarter;
	// this.entryPoint = isEntryPoint;
	// this.superClass = superClass;
	// this.superInterfaces = superInterfaces.clone();
	// this.execPermissions = execPermissions;
	// this.name = binding == null ? "<anonymous>" : binding
	// .getQualifiedName();
	// }
	//
	// New interface
	public boolean isAnonymous() {
		return this.anonymous;
	}

	public boolean isThreadMarker() {
		return threadMarker.value;
	}

	public boolean isEntryPoint() {
		return entryPoint;
	}

	public boolean isThreadStarter() {
		return threadStarter;
	}

	/**
	 * @return
	 */
	public ArrayList<MarkedString> getSuperThreadMarkers() {
		return superThreadMarkers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.analysis.IExecPermissionsHost
	 * #getExecPermissions()
	 */
	public Permissions getExecPermissions() {
		return execPermissions;
	}

	public boolean isInheritedFromEntryPoint() {
		return inheritedFromEntryPoint;
	}

	public MarkedString getSuperClass() {
		return superClass;
	}

	public ITypeBinding getBinding() {
		return binding;
	}

	public MarkedString[] getSuperInterfaces() {
		MarkedString[] result = new MarkedString[superInterfaces.size()];
		superInterfaces.toArray(result);
		return result;
	}

	public MarkedString getName() {
		return name;
	}

	public boolean isInterface() {
		return iface;
	}

	public boolean isClass() {
		return clazz;
	}

	public boolean isAnnotation() {
		return annot;
	}

	@Override
	public String toString() {
		return name.value;
	}

	@Override
	public void acceptVisitor(SymbolVisitor visitor) {
		visitor.visit(this);
	}

}
