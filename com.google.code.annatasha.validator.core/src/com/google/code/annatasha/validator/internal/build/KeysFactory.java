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

import java.security.InvalidParameterException;
import java.util.HashMap;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

public final class KeysFactory {

	private final static HashMap<IBinding, String> bindingsCache = new HashMap<IBinding, String>();

	public static String getKey(IMethodBinding binding) {
		String result = bindingsCache.get(binding);
		if (result == null) {
			StringBuilder mb = new StringBuilder();

			mb.append(getKey(binding.getDeclaringClass()));
			mb.append(binding.getName()).append(";(");
			for (ITypeBinding param : binding.getParameterTypes()) {
				mb.append(getKey(param)).append(",");
			}
			mb.append(')');
			// mb.append(getKey(binding.getReturnType())).append('|');

			result = mb.toString();
			bindingsCache.put(binding, result);
		}
		return result;
	}

	public static String getKey(ITypeBinding binding) {
		return Signature.createTypeSignature(ModelValidator.getCorrectBinding(
				binding.getTypeDeclaration()).getQualifiedName(), true);
	}

	public static String getKey(IVariableBinding binding) {
		String result = bindingsCache.get(binding);
		if (result == null) {
			StringBuilder mb = new StringBuilder();
			if (binding.isField()) {
				if (binding.getDeclaringClass() == null
						&& binding.getName().equals("length")) {
					mb.append("[.length");
				} else {
					mb.append(getKey(binding.getDeclaringClass()));
					mb.append(binding.getName());
				}
			} else {
				mb.append(getKey(binding.getDeclaringMethod()));
				mb.append(binding.getName());
			}
			result = mb.toString();
			bindingsCache.put(binding, result);
		}
		return result;
	}

	public static String getKey(IType type) {
		return Signature
				.createTypeSignature(type.getFullyQualifiedName(), true);
	}

	public static String getKey(IMethod method) {
		StringBuilder mb = new StringBuilder();

		mb.append(getKey(method.getDeclaringType()));
		mb.append(method.getElementName()).append(";(");
		for (String param : method.getParameterTypes()) {
			mb.append(param).append(",");
		}
		mb.append(')');

		return mb.toString();
	}

	public static String getKey(IBinding binding) {
		switch (binding.getKind()) {
		case IBinding.TYPE:
			return getKey((ITypeBinding) binding);

		case IBinding.METHOD:
			return getKey((IMethodBinding) binding);

		case IBinding.VARIABLE:
			return getKey((IVariableBinding) binding);
		}
		throw new InvalidParameterException(binding.toString());
	}

}
