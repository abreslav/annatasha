/**
 * 
 */
package com.google.code.annatasha.validator.internal.build;

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.google.code.annatasha.validator.internal.build.tasks.FieldTaskNode;
import com.google.code.annatasha.validator.internal.build.tasks.MethodTaskNode;
import com.google.code.annatasha.validator.internal.build.tasks.TaskNode;
import com.google.code.annatasha.validator.internal.build.tasks.TypeTaskNode;

final class SourceFileLightProcessor extends ASTVisitor {

	private final IResource resource;
	private final Set<TaskNode> tasks;

	/**
	 * @param resource
	 * @param listener
	 */
	public SourceFileLightProcessor(IResource resource, final Set<TaskNode> tasks) {
		this.resource = resource;
		this.tasks = tasks;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		ITypeBinding binding = node.resolveBinding();
		TypeTaskNode task = new TypeTaskNode(resource, node, binding);
		tasks.add(task);
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		IMethodBinding binding = node.resolveBinding();
		MethodTaskNode task = new MethodTaskNode(resource, node, binding);
		tasks.add(task);
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		IVariableBinding binding = node.resolveBinding();
		if (binding.isField()) {
			FieldTaskNode task = new FieldTaskNode(resource, node.getParent(), binding);
			tasks.add(task);
		}
		return true;
	}

}
