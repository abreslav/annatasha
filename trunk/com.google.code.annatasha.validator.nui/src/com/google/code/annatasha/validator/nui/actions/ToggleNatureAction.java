package com.google.code.annatasha.validator.nui.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.google.code.annatasha.validator.core.AnnatashaCore;

public class ToggleNatureAction implements IObjectActionDelegate {

	private ArrayList<IJavaProject> projects = new ArrayList<IJavaProject>();
	private boolean checked = false;
	private boolean allsame = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		boolean doSet = allsame && !checked;
		for (IJavaProject project : projects) {
			try {
				if (doSet) {
					AnnatashaCore.addProject(project);
				} else {
					AnnatashaCore.removeProject(project);
				}
			} catch (CoreException ex) {
				// XXX report exception here!!!
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@SuppressWarnings("unchecked")
	public void selectionChanged(IAction action, ISelection selection) {
		boolean checked = false;
		boolean alltrue = true;
		boolean allfalse = true;
		this.projects.clear();
		if (selection instanceof IStructuredSelection) {
			for (Iterator it = ((IStructuredSelection) selection).iterator(); it
					.hasNext();) {
				Object element = it.next();
				IJavaProject project = null;
				if (element instanceof IJavaProject) {
					project = (IJavaProject) element;
				} else if (element instanceof IAdaptable) {
					project = (IJavaProject) ((IAdaptable) element)
							.getAdapter(IJavaProject.class);
				}
				if (project != null) {
					projects.add(project);
					boolean isproj = AnnatashaCore.isProject(project);
					checked |= isproj;
					alltrue &= isproj;
					allfalse &= !isproj;
				}
			}
		}

		this.checked = checked;
		this.allsame = alltrue || allfalse;
		action.setChecked(checked);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.
	 * action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

}
