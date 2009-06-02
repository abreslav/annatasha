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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import com.google.code.annatasha.validator.core.AnnatashaCore;
import com.google.code.annatasha.validator.core.AnnatashaProject;
import com.google.code.annatasha.validator.internal.build.AnnatashaModel;
import com.google.code.annatasha.validator.internal.build.ClassNames;
import com.google.code.annatasha.validator.internal.build.Error;
import com.google.code.annatasha.validator.internal.build.IModelResolver;
import com.google.code.annatasha.validator.internal.build.IReportListener;
import com.google.code.annatasha.validator.internal.build.KeysFactory;
import com.google.code.annatasha.validator.internal.build.ModelValidator;
import com.google.code.annatasha.validator.internal.build.StringComparator;
import com.google.code.annatasha.validator.internal.build.markers.IMarkerFactory;
import com.google.code.annatasha.validator.internal.build.markers.MarkedBoolean;
import com.google.code.annatasha.validator.internal.build.markers.MarkedString;
import com.google.code.annatasha.validator.internal.build.markers.NullMarkerFactory;
import com.google.code.annatasha.validator.internal.build.markers.ProjectMarkerFactory;
import com.google.code.annatasha.validator.internal.build.symbols.FieldInformation;
import com.google.code.annatasha.validator.internal.build.symbols.MethodInformation;
import com.google.code.annatasha.validator.internal.build.symbols.SymbolInformation;
import com.google.code.annatasha.validator.internal.build.symbols.TypeInformation;

public final class ProjectBuilder implements ISourceFileRequestorCallback {

	private final AnnatashaModel model;
	private final AnnatashaProject project;
	private final IModelResolver resolver;
	private final IReportListener listener;

	public ProjectBuilder(AnnatashaModel model, AnnatashaProject project,
			IModelResolver resolver, IReportListener listener) {
		this.model = model;
		this.project = project;
		this.resolver = resolver;
		this.listener = listener;
	}

	public void syncProject(IProgressMonitor monitor) {
		try {
			processSources();
			processClassesAndPrimitives();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private IPath currentPath = null;
	private HashSet<String> definedSymbols = new HashSet<String>();
	private HashMap<String, IBinding> requestedSymbols = new HashMap<String, IBinding>();

	private void processSources() throws JavaModelException, CoreException {
		IJavaProject javaProject = project.getJavaProject();
		if (javaProject == null)
			return;

		// Parse source files
		final ArrayList<ICompilationUnit> unitsList = new ArrayList<ICompilationUnit>();
		for (IPackageFragment fragment : javaProject.getPackageFragments()) {
			for (ICompilationUnit unit : fragment.getCompilationUnits()) {
				IResource res = unit.getResource();
				if (res instanceof IFile) {
					IFile file = (IFile) res;
					final IPath fullPath = file.getFullPath();
					SourceFilePolicy info = project.sourcesInfo.get(fullPath);
					if (info == null) {
						info = new SourceFilePolicy(file);
						project.sourcesInfo.put(fullPath, info);
					}
					if (info.hasChanged()) {
						file.deleteMarkers(AnnatashaCore.MARKER_TYPE, true,
								IResource.DEPTH_INFINITE);
						unitsList.add(unit);
					}
				}
			}
		}

		ICompilationUnit[] units = new ICompilationUnit[unitsList.size()];
		units = unitsList.toArray(units);

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setProject(javaProject);

		SourceFileRequestor requestor = new SourceFileRequestor(this);

		parser.createASTs(units, new String[0], requestor, null);
	}

	void processClassesAndPrimitives() throws JavaModelException {
		HashMap<String, IClassFile> map = null;

		String keys[] = getUndefined();
		while (keys.length != 0) {

			if (map == null) {
				map = new HashMap<String, IClassFile>();
				for (IPackageFragment fragment : project.getJavaProject()
						.getPackageFragments()) {
					for (IClassFile file : fragment.getClassFiles()) {
						String key = file.getType().getFullyQualifiedName('.');
						map.put(key, file);
					}
				}
			}

			ClassFileProcessor processor = new ClassFileProcessor(this);
			for (String key : keys) {
				SymbolInformation info = resolver.getSymbolInformation(key);
				// break;
				if (info != null && !info.getSourcePolicy().hasChanged())
					continue;

				IBinding binding = getBinding(key);
				switch (binding.getKind()) {
				case IBinding.TYPE:
					ITypeBinding type = (ITypeBinding) binding;
					while (type.isArray()) {
						if (resolver.getTypeInformation(KeysFactory
								.getKey(type)) == null) {
							TypeInformation arrayType = new TypeInformation(
									resolver, KeysFactory.getKey(type),
									new NullSourcePolicy());
							arrayType.annot = false;
							arrayType.binding = type;
							arrayType.clazz = false;
							arrayType.entryPoint = false;
							arrayType.execPermissions = null;
							arrayType.hasMethods = false;
							arrayType.inheritedFromEntryPoint = false;
							arrayType.name = new MarkedString(
									new NullMarkerFactory(), type
											.getQualifiedName());
							arrayType.superClass = null;
							arrayType.superInterfaces = new ArrayList<MarkedString>();
							arrayType.superThreadMarkers = new ArrayList<MarkedString>();
							arrayType.threadMarker = new MarkedBoolean(
									new NullMarkerFactory(), false);
							arrayType.threadStarter = false;
							resolver.setTypeInformation(KeysFactory
									.getKey(type), arrayType);
							symbolDefined(KeysFactory.getKey(type));
						}
						type = type.getComponentType();
					}
					if (resolver.getTypeInformation(KeysFactory.getKey(type)) == null) {
						if (!type.isPrimitive()
								&& !type.isEnum()
								&& !type.getQualifiedName().startsWith(
										ClassNames.PACKAGE_PREFIX)) {
							IClassFile file = map.get(type.getQualifiedName());
							if (file == null) {
								listener.reportProblem(
										new ProjectMarkerFactory(project
												.getProject(), type.getName()),
										Error.SymbolUndefined);
							} else {
								processor.processType(file, type);
							}
						}
					}
					break;

				case IBinding.METHOD:
					IMethodBinding method = (IMethodBinding) binding;
					{
						IClassFile file = map.get(ModelValidator
								.getCorrectBinding(method.getDeclaringClass())
								.getQualifiedName());
						if (file == null) {
							listener.reportProblem(new ProjectMarkerFactory(
									project.getProject(), method.getName()),
									Error.SymbolUndefined);
						} else {
							processor.processMethod(file, method);
						}

					}
					break;
				}
			}

			String[] undefined = getUndefined();
			if (undefined.length == keys.length) {
				Arrays.sort(undefined, StringComparator.INSTANCE);
				Arrays.sort(keys, StringComparator.INSTANCE);
				boolean diff = false;
				for (int i = 0; i < undefined.length; ++i) {
					if (!undefined[i].equals(keys[i])) {
						diff = true;
						break;
					}
				}
				if (!diff)
					break;
			}
			keys = undefined;
		}
	}

	public String[] getUndefined() {
		HashSet<String> undefined = new HashSet<String>(requestedSymbols
				.keySet());
		undefined.removeAll(definedSymbols);
		String[] result = new String[undefined.size()];
		undefined.toArray(result);
		return result;
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
			SourceFilePolicy sourceFileInformation = project.sourcesInfo
					.get(currentPath);
			if (sourceFileInformation == null) {
				sourceFileInformation = new SourceFilePolicy((IFile) source
						.getResource());
				project.sourcesInfo.put(currentPath, sourceFileInformation);
			} else {
				// Remove all found symbols in source file
				sourceFileInformation.symbols.removeAll(definedSymbols);

				// Remove obsolete symbols
				for (String symbolName : sourceFileInformation.symbols) {
					resolver.removeSymbolInformation(symbolName);
				}

				// Clear source file symbols
				sourceFileInformation.symbols.clear();
			}
			sourceFileInformation.symbols.addAll(definedSymbols);
			sourceFileInformation.markSynchronised();
		}
	}

	public FieldInformation getFieldInformation(String key) {
		FieldInformation info = resolver.getFieldInformation(key);
		if (info == null) {
			info = new FieldInformation(resolver, key,
					new DelayedSourcePolicy());
			resolver.setFieldInformation(key, info);
		}
		return info;
	}

	public MethodInformation getMethodInformation(String key) {
		MethodInformation info = resolver.getMethodInformation(key);
		if (info == null) {
			info = new MethodInformation(resolver, key,
					new DelayedSourcePolicy());
			resolver.setMethodInformation(key, info);
		}
		return info;
	}

	public TypeInformation getTypeInformation(String key) {
		TypeInformation info = resolver.getTypeInformation(key);
		if (info == null) {
			info = new TypeInformation(resolver, key, new DelayedSourcePolicy());
			resolver.setTypeInformation(key, info);
		}
		return info;
	}

	public void enqueueMethodBodyValidation(IResource resource,
			IMethodBinding binding, ASTNode body) {
		model.getValidator().enqueueMethodBodyValidation(resource, binding,
				body);
	}

	public void symbolDefined(String key) {
		definedSymbols.add(key);
	}

	public boolean isDefined(String key) {
		return definedSymbols.contains(key);
	}

	public IBinding getBinding(String key) {
		return requestedSymbols.get(key);
	}

	public void symbolRequested(IBinding binding) {
		requestedSymbols.put(KeysFactory.getKey(binding), binding);
	}

	public void reportProblem(IMarkerFactory markerFactory, Error error) {
		listener.reportProblem(markerFactory, error);
	}
}
