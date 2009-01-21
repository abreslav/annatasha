/**
 * 
 */
package com.google.code.annatasha.validator.internal.build;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

final class AnnatashaVisitor extends ASTVisitor {

	public interface Listener {
		void acceptTypeDeclaration(AnnatashaVisitor visitor, TypeDeclaration node);

		void acceptMethodDeclaration(AnnatashaVisitor visitor, MethodDeclaration node);

		void acceptFieldDeclaration(AnnatashaVisitor visitor, FieldDeclaration node);
	}

	private final Listener listener;
	private final IResource resource;

	/**
	 * @param resource 
	 * @param listener
	 */
	public AnnatashaVisitor(IResource resource, Listener listener) {
		assert listener != null;

		this.resource = resource;
		this.listener = listener;
	}
	
	public IResource getResource() {
		return resource;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		listener.acceptTypeDeclaration(this, node);
		return true;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		listener.acceptMethodDeclaration(this, node);
		return true;
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		listener.acceptFieldDeclaration(this, node);
		return true;
	}

}