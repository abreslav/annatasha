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
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
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
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import com.google.code.annatasha.validator.core.AnnatashaCore;

public final class AnnatashaModel {

	private ArrayList<IJavaProject> projects = new ArrayList<IJavaProject>();
	IAnnatashaModelResolver resolver = new AnnatashaModelResolver();
	HashMap<IPath, SourceFileInformation> sourcesInfo = new HashMap<IPath, SourceFileInformation>();
	private final ModelProcessor modelProcessor = new ModelProcessor(this);

	public void addProject(IJavaProject project) throws CoreException {
		IProjectDescription description = project.getProject().getDescription();
		String[] natures = description.getNatureIds();

		for (int i = 0; i < natures.length; ++i) {
			if (AnnatashaCore.NATURE_ID.equals(natures[i])) {
				// Found the nature!
				return;
			}
		}

		// Not found. Add it.
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = AnnatashaCore.NATURE_ID;
		description.setNatureIds(newNatures);
		project.getProject().setDescription(description, null);

		projects.add(project);
		// sync(null);
	}

	public void removeProject(IJavaProject project) throws CoreException {
		IProjectDescription description = project.getProject().getDescription();
		String[] natures = description.getNatureIds();

		for (int i = 0; i < natures.length; ++i) {
			if (AnnatashaCore.NATURE_ID.equals(natures[i])) {
				// Remove the nature
				String[] newNatures = new String[natures.length - 1];
				System.arraycopy(natures, 0, newNatures, 0, i);
				System.arraycopy(natures, i + 1, newNatures, i, natures.length
						- i - 1);
				description.setNatureIds(newNatures);
				project.getProject().setDescription(description, null);

				projects.remove(project);
				return;
			}
		}
	}

	public boolean isProject(IJavaProject project) {
		IProjectDescription description;
		try {
			description = project.getProject().getDescription();
		} catch (CoreException e) {
			return false;
		}
		String[] natures = description.getNatureIds();

		for (int i = 0; i < natures.length; ++i) {
			if (AnnatashaCore.NATURE_ID.equals(natures[i])) {
				return true;
			}
		}
		return false;
	}

	public void sync(IProgressMonitor monitor) throws CoreException {
		for (IJavaProject project : projects) {
			try {
				processSources(monitor, project);
				processClassesAndPrimitives(project);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// try {
		try {
			modelProcessor.revalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// } catch (CircularReferenceException e) {
		// TODO report problem here
		// e.printStackTrace();
		// }

		// TODO finish sync method
		// throw new UnsupportedOperationException("not implemented");

	}

	private void processClassesAndPrimitives(IJavaProject project)
			throws JavaModelException {
		HashMap<String, IClassFile> map = null;

		String keys[] = modelProcessor.getUndefined();
		while (keys.length != 0) {

			if (map == null) {
				map = new HashMap<String, IClassFile>();
				for (IPackageFragment fragment : project.getPackageFragments()) {
					for (IClassFile file : fragment.getClassFiles()) {
						String key = file.getType().getFullyQualifiedName('.');
						// if (key.contains("Comparable")) {
						// System.err.println(key);
						// }
						map.put(key, file);
					}
				}
			}

			ClassFileProcessor processor = new ClassFileProcessor(
					modelProcessor);
			for (String key : keys) {
				SymbolInformation info = resolver.getSymbolInformation(key);
				// break;
				if (info != null && !info.getSourcePolicy().hasChanged())
					continue;

				IBinding binding = modelProcessor.getBinding(key);
				switch (binding.getKind()) {
				case IBinding.TYPE:
					ITypeBinding type = (ITypeBinding) binding;
					while (type.isArray()) {
						modelProcessor.symbolDefined(type.getKey());
						type = type.getComponentType();
					}
					if (type.isPrimitive()) {
						definePrimitiveType(type, project.getProject());
					} else {
						IClassFile file = map.get(type.getQualifiedName());
						if (file == null) {
							reportProblem(new ProjectMarkerFactory(project
									.getProject(), type.getName()),
									Error.SymbolUndefined);
						} else {
							processor.processType(file, type);
						}
					}
					break;

				case IBinding.METHOD:
					IMethodBinding method = (IMethodBinding) binding;
					{
						IClassFile file = map.get(ModelProcessor
								.getCorrectBinding(method.getDeclaringClass())
								.getQualifiedName());
						if (file == null) {
							reportProblem(new ProjectMarkerFactory(project
									.getProject(), method.getName()),
									Error.SymbolUndefined);
						} else {
							processor.processMethod(file, method);
						}

					}
					break;
				}
			}

			String[] undefined = modelProcessor.getUndefined();
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

	private void processSources(IProgressMonitor monitor, IJavaProject project)
			throws JavaModelException, CoreException {
		// Parse source files
		final ArrayList<ICompilationUnit> unitsList = new ArrayList<ICompilationUnit>();
		for (IPackageFragment fragment : project.getPackageFragments()) {
			for (ICompilationUnit unit : fragment.getCompilationUnits()) {
				IResource res = unit.getResource();
				if (res instanceof IFile) {
					IFile file = (IFile) res;
					final IPath fullPath = file.getFullPath();
					SourceFileInformation info = sourcesInfo.get(fullPath);
					if (info == null) {
						info = new SourceFileInformation(file);
						sourcesInfo.put(fullPath, info);
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
		parser.setProject(project);

		modelProcessor.reset();
		SourceFileRequestor requestor = new SourceFileRequestor(modelProcessor);

		parser.createASTs(units, new String[0], requestor, monitor);
	}

	private void definePrimitiveType(ITypeBinding type, IProject project) {
		modelProcessor.symbolDefined(type.getKey());
		TypeInformation typeInformation = modelProcessor
				.getTypeInformation(type.getKey());
		typeInformation.binding = type;
		typeInformation.annot = false;
		typeInformation.clazz = false;
		typeInformation.entryPoint = false;
		typeInformation.execPermissions = null;
		typeInformation.hasMethods = false;
		typeInformation.iface = false;
		typeInformation.inheritedFromEntryPoint = false;
		typeInformation.name = new MarkedString(new NullMarkerFactory(), type
				.getName());
		typeInformation.superClass = null;
		typeInformation.superInterfaces = new ArrayList<MarkedString>();
		typeInformation.superThreadMarkers = new ArrayList<MarkedString>();
		typeInformation.threadMarker = new MarkedBoolean(
				new NullMarkerFactory(), false);
		typeInformation.threadStarter = false;
	}

	void reportProblem(IMarkerFactory markerFactory, Error error) {
		try {
			markerFactory.createMarker(error.code, IMarker.SEVERITY_ERROR,
					error.message);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// throw new UnsupportedOperationException("not implemented");

	}

	public void clean() {
		for (IJavaProject project : projects) {
			try {
				project.getProject().deleteMarkers(AnnatashaCore.MARKER_TYPE,
						true, IResource.DEPTH_INFINITE);
			} catch (CoreException e) {
			}
		}
		this.resolver.clear();
		this.modelProcessor.clear();
		this.sourcesInfo.clear();
	}

}
