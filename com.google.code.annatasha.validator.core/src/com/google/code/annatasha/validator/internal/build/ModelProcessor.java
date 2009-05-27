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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

final class ModelProcessor implements ISourceFileRequestorCallback,
		IValidatorRequestsCallback, IReportListener {
	private final class RevalidationVisitor implements SymbolVisitor {
		CircularReferenceException exception = null;

		public void visit(TypeInformation info) {
			try {
				exception = null;
				ModelProcessor.this.revalidate(info);
			} catch (CircularReferenceException e) {
				exception = e;
			}
		}

		public void visit(MethodInformation info) {
			try {
				exception = null;
				ModelProcessor.this.revalidate(info);
			} catch (CircularReferenceException e) {
				exception = e;
			}
		}

		public void visit(FieldInformation info) {
			try {
				exception = null;
				ModelProcessor.this.revalidate(info);
			} catch (CircularReferenceException e) {
				exception = e;
			}
		}
	}

	private IPath currentPath = null;
	private AnnatashaModel annatashaModel;

	// private AnnatashaValidator validator = new AnnatashaValidator(this);

	public ModelProcessor(AnnatashaModel model) {
		this.annatashaModel = model;
	}

	public void sourceAccepted(ICompilationUnit source) {
		try {
			System.out.println("Accepting source: " + source.getElementName());

			IFile resource = (IFile) source.getResource();
			currentPath = resource.getFullPath();
		} catch (ClassCastException ex) {
			currentPath = null;
		}
	}

	public void sourceUpdated(ICompilationUnit source) {
		if (currentPath != null) {
			SourceFileInformation sourceFileInformation = annatashaModel.sourcesInfo
					.get(currentPath);
			if (sourceFileInformation == null) {
				sourceFileInformation = new SourceFileInformation(
						(IFile) source.getResource());
				annatashaModel.sourcesInfo.put(currentPath,
						sourceFileInformation);
			} else {
				// Remove all found symbols in source file
				sourceFileInformation.symbols.removeAll(definedSymbols);

				// Remove obsolete symbols
				for (String symbolName : sourceFileInformation.symbols) {
					annatashaModel.resolver.removeSymbolInformation(symbolName);
				}

				// Clear source file symbols
				sourceFileInformation.symbols.clear();
			}
			sourceFileInformation.symbols.addAll(definedSymbols);
			sourceFileInformation.markSynchronised();
		}
	}

	// public void enqueueRevalidation(Iterable<String> symbols) {
	// for (String symbol : symbols) {
	// if (!revalidationSet.contains(symbol)) {
	// revalidationQueue.add(symbol);
	// revalidationSet.add(symbol);
	// }
	// }
	// }
	//
	public FieldInformation getFieldInformation(String key) {
		FieldInformation info = annatashaModel.resolver
				.getFieldInformation(key);
		if (info == null) {
			info = new FieldInformation(annatashaModel.resolver, key,
					new DelayedSourcePolicy());
			annatashaModel.resolver.setFieldInformation(key, info);
		}
		return info;
	}

	public MethodInformation getMethodInformation(String key) {
		MethodInformation info = annatashaModel.resolver
				.getMethodInformation(key);
		if (info == null) {
			info = new MethodInformation(annatashaModel.resolver, key,
					new DelayedSourcePolicy());
			annatashaModel.resolver.setMethodInformation(key, info);
		}
		return info;
	}

	public TypeInformation getTypeInformation(String key) {
		TypeInformation info = annatashaModel.resolver.getTypeInformation(key);
		if (info == null) {
			info = new TypeInformation(annatashaModel.resolver, key,
					new DelayedSourcePolicy());
			annatashaModel.resolver.setTypeInformation(key, info);
		}
		return info;
	}

	public void reportProblem(IMarkerFactory markerFactory, Error error) {
		annatashaModel.reportProblem(markerFactory, error);
	}

	private HashSet<String> revalidatingSet = new HashSet<String>();
	private HashSet<String> revalidatedSet = new HashSet<String>();
	private RevalidationVisitor visitor = new RevalidationVisitor();

	void revalidate() {
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
		MethodBodyVerifier verifier = new MethodBodyVerifier(
				annatashaModel.resolver, this);
		while (!methodBodyValidationQueue.isEmpty()) {
			MethodBodyValidationRequest method = methodBodyValidationQueue
					.poll();
			MethodInformation information = annatashaModel.resolver
					.getMethodInformation(method.binding.getMethodDeclaration()
							.getKey());
			if (information != null)
				verifier.validate(method.resource, information, method.body);
		}
	}

	private void revalidateModel() {
		for (SymbolInformation symbol : annatashaModel.resolver) {
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
			revalidateSymbol(annatashaModel.resolver
					.getSymbolInformation(info.superClass.value));
		}
		for (MarkedString iface : info.superInterfaces) {
			revalidateSymbol(annatashaModel.resolver
					.getSymbolInformation(iface.value));
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
				TypeInformation superInterface = (TypeInformation) annatashaModel.resolver
						.getSymbolInformation(iface.value);
				if (superInterface == null) {
					reportProblem(iface.getMarkerFactory(),
							Error.SymbolUndefined);
				} else if (!superInterface.threadMarker.value) {
					reportProblem(iface.getMarkerFactory(),
							Error.ThreadMarkerInvalidInheritance);
				}
			}

			info.inheritedFromEntryPoint = false;
			info.superThreadMarkers = new ArrayList<MarkedString>(
					info.superThreadMarkers);
			info.threadStarter = false;
		} else {
			info.inheritedFromEntryPoint = false;
			info.superThreadMarkers = new ArrayList<MarkedString>();
			for (MarkedString iface : info.superInterfaces) {
				TypeInformation superInterface = (TypeInformation) annatashaModel.resolver
						.getSymbolInformation(iface.value);
				if (superInterface == null) {
					reportProblem(iface.getMarkerFactory(),
							Error.SymbolUndefined);
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
		ITypeBinding type = info.binding.getDeclaringClass();
		info.superDefinition = null;
		info.superDeclarations = new ArrayList<MarkedString>();

		if (!info.isStatic) {
			info.inheritedFromEntryPoint = false;
			while (type.getSuperclass() != null) {
				type = type.getSuperclass();
				for (IMethodBinding method : type.getDeclaredMethods()) {
					if (info.binding.isSubsignature(method)) {
						method = method.getMethodDeclaration();
						MethodInformation methodInformation = annatashaModel.resolver
								.getMethodInformation(method.getKey());
						if (methodInformation == null) {
							reportProblem(info.getName().getMarkerFactory(),
									Error.SymbolUndefined);
						} else {
							info.superDefinition = new MarkedString(
									methodInformation.getName()
											.getMarkerFactory(), method
											.getKey());
						}
						break;
					}
				}
			}

			Queue<ITypeBinding> queue = new LinkedList<ITypeBinding>();
			queue.addAll(Arrays.asList(info.binding.getDeclaringClass()
					.getInterfaces()));
			while (!queue.isEmpty()) {
				type = queue.poll();
				for (IMethodBinding method : type.getDeclaredMethods()) {
					if (info.binding.isSubsignature(method)) {
						method = method.getMethodDeclaration();
						MethodInformation methodInformation = annatashaModel.resolver
								.getMethodInformation(method.getKey());
						if (methodInformation == null) {
							reportProblem(info.getName().getMarkerFactory(),
									Error.SymbolUndefined);
						} else {
							info.superDeclarations.add(new MarkedString(
									methodInformation.getName()
											.getMarkerFactory(), method
											.getKey()));
						}
					} else {
						queue.addAll(Arrays.asList(type.getInterfaces()));
					}
				}
			}
		}

		if (info.superDefinition != null) {
			MethodInformation methodInformation = annatashaModel.resolver
					.getMethodInformation(info.superDefinition.value);
			revalidateSymbol(methodInformation);

			info.inheritedFromEntryPoint |= (methodInformation.entryPoint || methodInformation.inheritedFromEntryPoint);
		}

		for (MarkedString decl : info.superDeclarations) {
			MethodInformation methodInformation = annatashaModel.resolver
					.getMethodInformation(decl.value);
			revalidateSymbol(methodInformation);

			info.inheritedFromEntryPoint |= (methodInformation.entryPoint || methodInformation.inheritedFromEntryPoint);
		}

	}

	private HashSet<String> definedSymbols = new HashSet<String>();
	private HashMap<String, IBinding> requestedSymbols = new HashMap<String, IBinding>();
	private Queue<MethodBodyValidationRequest> methodBodyValidationQueue = new LinkedList<MethodBodyValidationRequest>();

	public void symbolDefined(String key) {
		definedSymbols.add(key);
	}

	public IBinding getBinding(String key) {
		return requestedSymbols.get(key);
	}

	public String[] getUndefined() {
		HashSet<String> undefined = new HashSet<String>(requestedSymbols
				.keySet());
		undefined.removeAll(definedSymbols);
		String[] result = new String[undefined.size()];
		undefined.toArray(result);
		return result;
	}

	public boolean isDefined(String key) {
		return definedSymbols.contains(key);
	}

	public void symbolRequested(IBinding binding) {
		requestedSymbols.put(binding.getKey(), binding);
	}

	public void reset() {
		definedSymbols.clear();
		requestedSymbols.clear();
		methodBodyValidationQueue.clear();
	}

	static ITypeBinding getCorrectBinding(ITypeBinding binding) {
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