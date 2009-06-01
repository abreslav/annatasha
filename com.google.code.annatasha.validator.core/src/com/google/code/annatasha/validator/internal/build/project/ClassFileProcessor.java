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

package com.google.code.annatasha.validator.internal.build.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;

import com.google.code.annatasha.validator.internal.build.ClassNames;
import com.google.code.annatasha.validator.internal.build.KeysFactory;
import com.google.code.annatasha.validator.internal.build.ModelValidator;
import com.google.code.annatasha.validator.internal.build.markers.ClassFileMarkerFactory;
import com.google.code.annatasha.validator.internal.build.markers.MarkedBoolean;
import com.google.code.annatasha.validator.internal.build.markers.MarkedString;
import com.google.code.annatasha.validator.internal.build.symbols.MethodInformation;
import com.google.code.annatasha.validator.internal.build.symbols.Permissions;
import com.google.code.annatasha.validator.internal.build.symbols.TypeInformation;

public final class ClassFileProcessor {

	private final ISourceFileRequestorCallback modelProcessor;

	public ClassFileProcessor(ISourceFileRequestorCallback modelProcessor) {
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

	public void processMethod(IClassFile file, IMethodBinding binding) {
		try {
			ClassFileMarkerFactory factory = new ClassFileMarkerFactory(file);
			IType type = file.getType();

			for (IMethod method : type.getMethods()) {
				if (!methodSignaturesEqual(KeysFactory.getKey(method),
						KeysFactory.getKey(binding)))
					continue;

				if (!method.exists())
					continue;

				String key = KeysFactory.getKey(binding);
				modelProcessor.symbolDefined(key);

				MethodInformation methodInformation = modelProcessor
						.getMethodInformation(key);
				((DelayedSourcePolicy) methodInformation.getSourcePolicy())
						.setSourcePolicy(new ClassFileSource(file));
				methodInformation.binding = binding;
				methodInformation.name = new MarkedString(factory, binding
						.getName());
				methodInformation.declaringType = KeysFactory
						.getKey(ModelValidator.getCorrectBinding(binding
								.getDeclaringClass()));
				methodInformation.isStatic = Flags.isStatic(method.getFlags())
						|| method.isConstructor();
				methodInformation.isConstructor = method.isConstructor();
				methodInformation.returnType = methodInformation.isConstructor ? new MarkedString(
						factory, KeysFactory
								.getKey(ModelValidator
										.getCorrectBinding(binding
												.getDeclaringClass())))
						: new MarkedString(factory, KeysFactory
								.getKey(ModelValidator
										.getCorrectBinding(binding
												.getReturnType())));

				if (binding.getReturnType() != null)
					modelProcessor.symbolRequested(ModelValidator
							.getCorrectBinding(binding.getReturnType()));

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

				return;
			}

		} catch (JavaModelException e) {
		}
	}

	private boolean methodSignaturesEqual(String lhs, String rhs) {
		return lhs.equals(rhs);
	}

	public void processType(IClassFile file, ITypeBinding typeBinding) {
		ClassFileMarkerFactory factory = new ClassFileMarkerFactory(file);
		IType type = file.getType();
		String key = KeysFactory.getKey(typeBinding);

		modelProcessor.symbolDefined(key);
		TypeInformation typeInformation = modelProcessor
				.getTypeInformation(key);
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
					ClassNames.EntryPoints[i].typeBindingKey = KeysFactory
							.getKey(type);
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
				ITypeBinding superBinding = ModelValidator
						.getCorrectBinding(typeBinding.getSuperclass());
				typeInformation.superClass = new MarkedString(factory,
						KeysFactory.getKey(superBinding));
				modelProcessor.symbolRequested(superBinding);
			}

			HashMap<String, ITypeBinding> superInterfaces = new HashMap<String, ITypeBinding>();
			for (ITypeBinding iface : typeBinding.getInterfaces()) {
				ITypeBinding superInterfaceBinding = ModelValidator
						.getCorrectBinding(iface);
				modelProcessor.symbolRequested(superInterfaceBinding);
				superInterfaces.put(superInterfaceBinding.getQualifiedName(),
						superInterfaceBinding);
			}

			for (String iface : type.getSuperInterfaceNames()) {
				typeInformation.superInterfaces.add(new MarkedString(factory,
						KeysFactory.getKey(superInterfaces.get(iface.replace(
								'$', '.')))));
			}
			typeInformation.hasMethods = type.getMethods().length != 0;
			typeInformation.execPermissions = null;
			IAnnotation permissions = type
					.getAnnotation(ClassNames.EXEC_PERMISSIONS);
			if (permissions != null && permissions.exists()) {
				typeInformation.execPermissions = getPermissions(factory,
						permissions);
			}

			for (IMethodBinding method : typeBinding.getDeclaredMethods()) {
				if (Modifier.isPublic(method.getModifiers()))
					processMethod(file, method);
			}
		} catch (JavaModelException e) {

		}
	}

	private Permissions getPermissions(ClassFileMarkerFactory factory,
			IAnnotation permissions) {
		// permissions.getMemberValuePairs()[0].;
		return null;
	}

}
