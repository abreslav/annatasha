/**
 * 
 */
package com.google.code.annatasha.validator.internal.build;

public interface ITaskVisitor {
	
	public void visit(TypeTaskNode typeTask);
	public void visit(MethodTaskNode methodTask);
	public void visit(FieldTaskNode fieldTask);
	
}