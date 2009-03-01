/**
 * 
 */
package com.google.code.annatasha.validator.internal.build;

import org.eclipse.core.runtime.CoreException;

public interface ITaskVisitor {
	
	public void visit(TypeTaskNode typeTask) throws CoreException;
	public void visit(MethodTaskNode methodTask) throws CoreException;
	public void visit(FieldTaskNode fieldTask) throws CoreException;
	
}