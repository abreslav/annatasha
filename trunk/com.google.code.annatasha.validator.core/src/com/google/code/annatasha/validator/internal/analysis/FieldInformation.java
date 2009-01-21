package com.google.code.annatasha.validator.internal.analysis;

import org.eclipse.jdt.core.dom.FieldDeclaration;

public final class FieldInformation {

	private boolean initialized = false;

	private FieldDeclaration ast;

	private Class<?>[] readPermissions;
	private Class<?>[] writePermissions;

	/**
	 * @param readPermissions
	 *            The list of thread classes allowed to read, <code>null</code>
	 *            if permissions are not set in which case everybody is allowed
	 *            to read
	 * @param writePermissions
	 *            The list of thread classes allowed to write, <code>null</code>
	 *            if permissions are not set in which case everybody is allowed
	 *            to write
	 */
	public void initialize(FieldDeclaration ast, Class<?>[] readPermissions,
			Class<?>[] writePermissions) {
		assert !initialized : "Re-initialization of FieldInformation is prohibited";

		this.ast = ast;
		
		this.readPermissions = readPermissions;
		this.writePermissions = writePermissions;

		this.initialized = true;
	}

	public Class<?>[] getReadPermissions() {
		assert initialized;

		return readPermissions;
	}

	public Class<?>[] getWritePermissions() {
		assert initialized;

		return writePermissions;
	}

	public boolean isInitialized() {
		return initialized;
	}
	
	public FieldDeclaration getAst() {
		return ast;
	}

}
