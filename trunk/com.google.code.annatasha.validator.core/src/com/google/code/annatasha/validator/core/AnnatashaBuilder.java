package com.google.code.annatasha.validator.core;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

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
			AnnatashaCore.getModel().sync(monitor);
		} else if (kind == CLEAN_BUILD) {
			AnnatashaCore.getModel().clean();
		}
			/*
		 * else { IResourceDelta delta = getDelta(getProject()); if (delta ==
		 * null) { fullBuild(monitor); } else { incrementalBuild(delta,
		 * monitor); } }
		 */
		return null;
	}
	
	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		AnnatashaCore.getModel().clean();
	}

}
