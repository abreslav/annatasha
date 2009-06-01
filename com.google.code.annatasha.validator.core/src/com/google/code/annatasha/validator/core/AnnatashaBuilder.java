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

package com.google.code.annatasha.validator.core;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.google.code.annatasha.validator.internal.build.project.ProjectBuilder;

public class AnnatashaBuilder extends IncrementalProjectBuilder {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("unchecked")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD || kind == INCREMENTAL_BUILD) {
			AnnatashaCore.ensureProject(getJavaProject());
			ProjectBuilder builder = new ProjectBuilder(AnnatashaCore
					.getModel(), AnnatashaCore.getProject(getJavaProject()),
					AnnatashaCore.getModel().getResolver(), AnnatashaCore
							.getModel());
			builder.syncProject(monitor);
			AnnatashaCore.getModel().getValidator().revalidate();
		} else if (kind == CLEAN_BUILD) {
			AnnatashaCore.getModel().clean();
		}
		return null;
	}

	private IJavaProject getJavaProject() throws CoreException {
		return (IJavaProject) getProject().getNature(JavaCore.NATURE_ID);
	}

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		AnnatashaCore.getModel().clean();
	}

}
