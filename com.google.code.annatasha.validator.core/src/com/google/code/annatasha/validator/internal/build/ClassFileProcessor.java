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

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

final class ClassFileProcessor {

	private final ModelProcessor modelProcessor;

	ClassFileProcessor(ModelProcessor modelProcessor) {
		this.modelProcessor = modelProcessor;
	}

	// void accept(IClassFile file) {
	// IType type = file.getType();
	// if (!type.exists())
	// return;
	//
	// ClassFileMarkerFactory factory = new ClassFileMarkerFactory(file);
	// processType(factory, type);
	// processFields(factory, type);
	// processMethods(factory, type);
	// }

	void processMethod(IClassFile file, IMethodBinding binding) {
		try {
			ClassFileMarkerFactory factory = new ClassFileMarkerFactory(file);
			IType type = file.getType();

			for (IMethod method : type.getMethods()) {
				if (!methodSignaturesEqual(method.getKey(), binding.getKey()))
					continue;

				if (!method.exists())
					continue;

				String key = binding.getKey();
				modelProcessor.symbolDefined(key);

				MethodInformation methodInformation = modelProcessor
						.getMethodInformation(key);
				((DelayedSourcePolicy) methodInformation.getSourcePolicy())
						.setSourcePolicy(new ClassFileSource(file));
				methodInformation.name = new MarkedString(factory, binding
						.getName());
				methodInformation.declaringType = ModelProcessor.getCorrectBinding(binding.getDeclaringClass()).getKey();
				methodInformation.isStatic = Flags.isStatic(method.getFlags())
						|| method.isConstructor();
				methodInformation.isConstructor = method.isConstructor();
				methodInformation.returnType = methodInformation.isConstructor ? new MarkedString(
						factory, ModelProcessor.getCorrectBinding(binding.getDeclaringClass()).getKey())
						: new MarkedString(factory, ModelProcessor.getCorrectBinding(
							binding.getReturnType()).getKey());

				if (binding.getReturnType() != null)
					modelProcessor.symbolRequested(ModelProcessor.getCorrectBinding( binding.getReturnType()));

				methodInformation.execPermissions = Permissions.Any;
				IAnnotation permissions = method
						.getAnnotation(ClassNames.EXEC_PERMISSIONS);
				if (permissions != null && permissions.exists()) {
					methodInformation.execPermissions = getPermissions(factory,
							permissions);
				}

				for (ClassNames.EntryPoint point : ClassNames.EntryPoints) {
					if (point.className.equals(type.getFullyQualifiedName())
							&& point.methodName.equals(method.getElementName())) {
						methodInformation.entryPoint = true;
						break;
					}
				}

				methodInformation.referencedBindings = new HashSet<String>();

				// Obtained during local lookup
				// public IMethodBinding binding;
				//
				// public MarkedString name;
				// public String declaringType;
				// public boolean isStatic;
				// public boolean isConstructor;
				//
				// public MarkedString returnType;
				// public Permissions execPermissions;
				// // The set of keys of thread starter parameters
				// public ArrayList<MarkedString> threadStarters = new
				// ArrayList<MarkedString>();
				// public boolean entryPoint;
				// public HashSet<String> referencedBindings;
				//
				return;
			}

		} catch (JavaModelException e) {
		}
	}

	private boolean methodSignaturesEqual(String lhs, String rhs) {
		String l, r;
		if (lhs.indexOf(')') != -1)
			l = lhs.substring(0, lhs.indexOf(')'));
		else
			l = lhs;

		if (rhs.indexOf(')') != -1)
			r = rhs.substring(0, rhs.indexOf(')'));
		else
			r = rhs;

		return l.equals(r);
	}

	// void processField(ClassFileMarkerFactory factory, IType type) {
	// try {
	// for (IField field : type.getFields()) {
	// if (!field.exists())
	// continue;
	//
	// String key = field.getKey();
	// modelProcessor.symbolDefined(key);
	// FieldInformation fieldInformation = modelProcessor
	// .getFieldInformation(key);
	//
	// fieldInformation.type = new MarkedString(factory, type.getKey());
	// fieldInformation.readPermissions = Permissions.Any;
	// fieldInformation.writePermissions = Permissions.Any;
	//
	// IAnnotation rp = field
	// .getAnnotation(ClassNames.READ_PERMISSIONS);
	// IAnnotation wp = field
	// .getAnnotation(ClassNames.WRITE_PERMISSIONS);
	// if (rp != null && rp.exists()) {
	// fieldInformation.readPermissions = getPermissions(factory,
	// rp);
	// }
	// if (wp != null && wp.exists()) {
	// fieldInformation.writePermissions = getPermissions(factory,
	// wp);
	// }
	// IAnnotation threadStarter = field
	// .getAnnotation(ClassNames.THREAD_STARTER);
	// fieldInformation.threadStarter = new MarkedBoolean(factory,
	// threadStarter != null && threadStarter.exists());
	//
	// // public MarkedString type;
	// //
	// // public Permissions readPermissions;
	// // public Permissions writePermissions;
	// //
	// // public MarkedBoolean threadStarter;
	//
	// }
	//
	// } catch (JavaModelException e) {
	// }
	// }
	//
	void processType(IClassFile file, ITypeBinding typeBinding) {
		ClassFileMarkerFactory factory = new ClassFileMarkerFactory(file);
		IType type = file.getType();

		modelProcessor.symbolDefined(typeBinding.getKey());
		TypeInformation typeInformation = modelProcessor
				.getTypeInformation(type.getKey());
		((DelayedSourcePolicy) typeInformation.getSourcePolicy())
				.setSourcePolicy(new ClassFileSource(file));
		typeInformation.name = new MarkedString(factory, type
				.getFullyQualifiedName());
		try {
			typeInformation.clazz = type.isClass();
			typeInformation.iface = type.isInterface() && !type.isAnnotation();
			typeInformation.annot = type.isAnnotation();
			for (int i = 0; i < ClassNames.EntryPoints.length; ++i) {
				if (ClassNames.EntryPoints[i].className.equals(type
						.getFullyQualifiedName())) {
					typeInformation.entryPoint = true;
					ClassNames.EntryPoints[i].typeBindingKey = type.getKey();
					break;
				}
			}
			IAnnotation annotation = type
					.getAnnotation(ClassNames.THREAD_MARKER);
			typeInformation.threadMarker = new MarkedBoolean(factory,
					annotation != null && annotation.exists());
			typeInformation.superClass = null;
			typeInformation.superInterfaces = new ArrayList<MarkedString>();
			String superSignature = type.getSuperclassTypeSignature();
			if (typeInformation.clazz && superSignature != null) {
				typeInformation.superClass = new MarkedString(factory,
						superSignature);
				modelProcessor.symbolRequested(ModelProcessor.getCorrectBinding(typeBinding.getSuperclass()));
			}
			for (String iface : type.getSuperInterfaceTypeSignatures()) {
				typeInformation.superInterfaces.add(new MarkedString(factory,
						iface));
			}
			for (ITypeBinding iface : typeBinding.getInterfaces()) {
				modelProcessor.symbolRequested(ModelProcessor.getCorrectBinding(iface));
			}
			typeInformation.hasMethods = type.getMethods().length != 0;
			typeInformation.execPermissions = null;
			IAnnotation permissions = type
					.getAnnotation(ClassNames.EXEC_PERMISSIONS);
			if (permissions != null && permissions.exists()) {
				typeInformation.execPermissions = getPermissions(factory,
						permissions);
			}

		} catch (JavaModelException e) {

		}

		// public ITypeBinding binding;
		// public MarkedString name;
		// public boolean clazz;
		// public boolean iface;
		// public boolean annot;
		// public boolean entryPoint;
		// public MarkedBoolean threadMarker;
		// public MarkedString superClass;
		// public ArrayList<MarkedString> superInterfaces;
		// public Permissions execPermissions;
		// public boolean hasMethods;
	}

	private Permissions getPermissions(ClassFileMarkerFactory factory,
			IAnnotation permissions) {
		// permissions.getMemberValuePairs()[0].;
		return null;
	}

}
