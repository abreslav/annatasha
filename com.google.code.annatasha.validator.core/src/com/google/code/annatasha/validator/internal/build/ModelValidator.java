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
package com.google.code.annatasha.validator.internal.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import com.google.code.annatasha.validator.internal.build.markers.IMarkerFactory;
import com.google.code.annatasha.validator.internal.build.markers.MarkedString;
import com.google.code.annatasha.validator.internal.build.project.CircularReferenceException;
import com.google.code.annatasha.validator.internal.build.symbols.FieldInformation;
import com.google.code.annatasha.validator.internal.build.symbols.MethodInformation;
import com.google.code.annatasha.validator.internal.build.symbols.Permissions;
import com.google.code.annatasha.validator.internal.build.symbols.SymbolInformation;
import com.google.code.annatasha.validator.internal.build.symbols.SymbolVisitor;
import com.google.code.annatasha.validator.internal.build.symbols.TypeInformation;

public final class ModelValidator implements IValidatorRequestsCallback,
		IReportListener {
	private final class RevalidationVisitor implements SymbolVisitor {
		CircularReferenceException exception = null;

		public void visit(TypeInformation info) {
			try {
				exception = null;
				ModelValidator.this.revalidate(info);
			} catch (CircularReferenceException e) {
				exception = e;
			}
		}

		public void visit(MethodInformation info) {
			try {
				exception = null;
				ModelValidator.this.revalidate(info);
			} catch (CircularReferenceException e) {
				exception = e;
			}
		}

		public void visit(FieldInformation info) {
			try {
				exception = null;
				ModelValidator.this.revalidate(info);
			} catch (CircularReferenceException e) {
				exception = e;
			}
		}
	}

	private AnnatashaModel model;

	// private AnnatashaValidator validator = new AnnatashaValidator(this);

	public ModelValidator(AnnatashaModel model) {
		this.model = model;
	}

	public void reportProblem(IMarkerFactory markerFactory, Error error) {
		model.reportProblem(markerFactory, error);
	}

	private HashSet<String> revalidatingSet = new HashSet<String>();
	private HashSet<String> revalidatedSet = new HashSet<String>();
	private RevalidationVisitor visitor = new RevalidationVisitor();

	public void revalidate() {
		revalidatedSet.clear();
		revalidatingSet.clear();

		try {
			revalidateModel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			revalidateMethodBodies();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void revalidateMethodBodies() {
		MethodBodyVerifier verifier = new MethodBodyVerifier(model
				.getResolver(), this);
		while (!methodBodyValidationQueue.isEmpty()) {
			MethodBodyValidationRequest method = methodBodyValidationQueue
					.poll();
			MethodInformation information = model.getResolver()
					.getMethodInformation(
							KeysFactory.getKey(method.binding
									.getMethodDeclaration()));
			if (information != null)
				verifier.validate(method.resource, information, method.body);
		}
	}

	private void revalidateModel() {
		for (SymbolInformation symbol : model.getResolver()) {
			try {
				revalidateSymbol(symbol);
			} catch (CircularReferenceException e) {
				IMarkerFactory factory = e.symbolInformation.getSourcePolicy()
						.getMarkerFactory(e.symbolInformation);
				reportProblem(factory, Error.CircularReference);
			}
		}
	}

	private void revalidateSymbol(SymbolInformation symbol)
			throws CircularReferenceException {
		if (symbol == null) {
			return;
		}
		String key = symbol.getKey();
		if (revalidatedSet.contains(key)) {
			return;
		}
		if (revalidatingSet.contains(key)) {
			throw new CircularReferenceException(symbol);
		}
		try {
			revalidatingSet.add(key);
			symbol.acceptVisitor(visitor);
			if (visitor.exception != null) {
				throw visitor.exception;
			}
		} finally {
			revalidatingSet.remove(key);
			revalidatedSet.add(key);
		}

	}

	private void revalidate(TypeInformation info)
			throws CircularReferenceException {
		if (info.superClass != null) {
			revalidateSymbol(model.getResolver().getSymbolInformation(
					info.superClass.value));
		}
		for (MarkedString iface : info.superInterfaces) {
			revalidateSymbol(model.getResolver().getSymbolInformation(
					iface.value));
		}
		if (info.threadMarker.value) {
			if (!info.iface) {
				reportProblem(info.threadMarker.getMarkerFactory(),
						Error.ThreadMarkerMustBeAnInterface);
			}
			if (info.hasMethods) {
				reportProblem(info.getName().getMarkerFactory(),
						Error.ThreadMarkerMustHaveNoMethods);
			}
			if (info.execPermissions != null) {
				reportProblem(info.getName().getMarkerFactory(),
						Error.ThreadMarkerCannotSpecifyExecPermissions);
			}
			for (MarkedString iface : info.superInterfaces) {
				TypeInformation superInterface = (TypeInformation) model
						.getResolver().getSymbolInformation(iface.value);
				if (superInterface == null) {
					// reportProblem(iface.getMarkerFactory(),
					// Error.SymbolUndefined);
				} else if (!superInterface.threadMarker.value) {
					reportProblem(iface.getMarkerFactory(),
							Error.ThreadMarkerInvalidInheritance);
				}
			}

			info.inheritedFromEntryPoint = false;
			info.superThreadMarkers = new ArrayList<MarkedString>(
					info.superInterfaces);
			info.threadStarter = false;
		} else {
			info.inheritedFromEntryPoint = false;
			info.superThreadMarkers = new ArrayList<MarkedString>();
			for (MarkedString iface : info.superInterfaces) {
				TypeInformation superInterface = model.getResolver()
						.getTypeInformation(iface.value);
				if (superInterface == null) {
					// reportProblem(iface.getMarkerFactory(),
					// Error.SymbolUndefined);
				} else {
					info.inheritedFromEntryPoint |= (superInterface.entryPoint || superInterface.inheritedFromEntryPoint);
					if (superInterface.threadMarker.value)
						info.superThreadMarkers.add(iface);
				}
			}

			if (info.iface) {
				if (info.superThreadMarkers.size() != 0) {
					reportProblem(info.getName().getMarkerFactory(),
							Error.ThreadMarkerMustBeSpecifiedExplicitly);
				}
				info.threadStarter = false;
			} else if (info.clazz) {
				if (info.superThreadMarkers.size() != 0) {
					info.threadStarter = true;
					if (info.superThreadMarkers.size() > 1) {
						reportProblem(
								info.getName().getMarkerFactory(),
								Error.ExactlyOneThreadMarkerExpectedForThreadStarter);
					}
					if (!info.inheritedFromEntryPoint) {
						reportProblem(info.getName().getMarkerFactory(),
								Error.ThreadStarterNotInheritedFromEntryPoint);
					}
				} else {
					info.threadStarter = false;
				}
			}
		}
	}

	private void revalidate(FieldInformation info)
			throws CircularReferenceException {
		// Nothing to do
	}

	private void revalidate(MethodInformation info)
			throws CircularReferenceException {
		TypeInformation typeInformation = model.getResolver()
				.getTypeInformation(info.declaringType);
		revalidateSymbol(typeInformation);

		ITypeBinding type = info.binding.getDeclaringClass();
		info.superDefinition = null;
		info.superDeclarations = new ArrayList<MarkedString>();
		// if (info.isConstructor && info.execPermissions != null
		// && !info.execPermissions.isAnonymous()) {
		// reportProblem(info.getName().getMarkerFactory(),
		// Error.ConstructorMightOnlyHaveAnyAccess);
		// }
		if (!info.isStatic) {
			info.inheritedFromEntryPoint = false;
			while (type.getSuperclass() != null) {
				type = type.getSuperclass();
				for (IMethodBinding method : type.getDeclaredMethods()) {
					if (info.binding.isSubsignature(method)) {
						method = method.getMethodDeclaration();
						MethodInformation superMethod = model.getResolver()
								.getMethodInformation(
										KeysFactory.getKey(method));
						if (superMethod == null) {
							// reportProblem(info.getName().getMarkerFactory(),
							// Error.SymbolUndefined);
						} else {
							info.superDefinition = new MarkedString(superMethod
									.getName().getMarkerFactory(), KeysFactory
									.getKey(method));
							if (superMethod.isEntryPoint()
									|| superMethod.isInheritedFromEntryPoint()) {
								info.inheritedFromEntryPoint = true;
							}
						}
						break;
					}
				}
			}

			if (info.binding.getDeclaringClass().getQualifiedName().equals(
					"java.util.concurrent.Executor")
					&& info.getName().value.equals("execute")) {
				info.threadStarters = new ArrayList<Integer>();
				info.threadStarters.add(0);
			}
			Queue<ITypeBinding> queue = new LinkedList<ITypeBinding>();
			queue.addAll(Arrays.asList(info.binding.getDeclaringClass()
					.getInterfaces()));
			while (!queue.isEmpty()) {
				type = queue.poll();
				for (IMethodBinding method : type.getDeclaredMethods()) {
					if (info.binding.isSubsignature(method)) {
						method = method.getMethodDeclaration();
						if (type.getQualifiedName().equals(
								"java.util.concurrent.Executor")
								&& method.getName().equals("execute")) {
							info.threadStarters = new ArrayList<Integer>();
							info.threadStarters.add(0);
						}
						MethodInformation superMethod = model.getResolver()
								.getMethodInformation(
										KeysFactory.getKey(method));
						if (superMethod == null) {
							// reportProblem(info.getName().getMarkerFactory(),
							// Error.SymbolUndefined);
						} else {
							info.superDeclarations.add(new MarkedString(
									superMethod.getName().getMarkerFactory(),
									KeysFactory.getKey(method)));
							if (superMethod.isEntryPoint()
									|| superMethod.isInheritedFromEntryPoint()) {
								info.inheritedFromEntryPoint = true;
							}
						}
					} else {
						queue.addAll(Arrays.asList(type.getInterfaces()));
					}
				}
			}
		}

		if (info.isInheritedFromEntryPoint()
				&& typeInformation.isThreadStarter()) {
			if (info.execPermissions != null) {
				reportProblem(info.getName().getMarkerFactory(),
						Error.ExecPermissionsInThreadStarterMethod);
			}
			if (typeInformation.superThreadMarkers != null
					&& typeInformation.superThreadMarkers.size() == 1) {
				TypeInformation superThreadMarker = model
						.getResolver()
						.getTypeInformation(
								typeInformation.superThreadMarkers.get(0).value);
				info.execPermissions = superThreadMarker == null ? Permissions.Any
						: new Permissions(Arrays.asList(new MarkedString(info
								.getName().getMarkerFactory(),
								superThreadMarker.getKey())));
			}
		} else {
			if (info.execPermissions == null && typeInformation != null
					&& typeInformation.execPermissions != null)
				info.execPermissions = typeInformation.execPermissions;
			if (info.execPermissions == null)
				info.execPermissions = Permissions.Any;

			boolean marked = false;
			if (info.superDefinition != null) {
				MethodInformation superMethod = model.getResolver()
						.getMethodInformation(info.superDefinition.value);
				revalidateSymbol(superMethod);

				if (!Permissions.mightAccess(model.getResolver(),
						superMethod.execPermissions, info.execPermissions)) {
					reportProblem(info.getName().getMarkerFactory(),
							Error.ExecPermissionsInheritedViolation);
					marked = true;
				}
			}

			if (!marked) {
				for (MarkedString decl : info.superDeclarations) {
					MethodInformation superMethod = model.getResolver()
							.getMethodInformation(decl.value);
					revalidateSymbol(superMethod);

					if (!Permissions.mightAccess(model.getResolver(),
							superMethod.execPermissions, info.execPermissions)) {
						reportProblem(info.getName().getMarkerFactory(),
								Error.ExecPermissionsInheritedViolation);
						break;
					}
				}
			}
		}

	}

	private Queue<MethodBodyValidationRequest> methodBodyValidationQueue = new LinkedList<MethodBodyValidationRequest>();

	public void reset() {
		methodBodyValidationQueue.clear();
	}

	public static ITypeBinding getCorrectBinding(ITypeBinding binding) {
		ITypeBinding type = binding.getTypeDeclaration();
		// while (type.isArray()) {
		// type = type.getComponentType();
		// }
		return type.getErasure();
	}

	public void clear() {
	}

	public void enqueueMethodBodyValidation(IResource resource,
			IMethodBinding binding, ASTNode body) {
		methodBodyValidationQueue.add(new MethodBodyValidationRequest(resource,
				binding, body));
	}

}