/**
 * 
 */
package com.google.code.annatasha.validator.internal.analysis;

import java.util.Collection;

/**
 * @author Ivan Egorov
 * 
 */
public final class MethodInformation {

	private final TypeInformation type;
	private final MethodInformation superDefinition;
	private final MethodInformation[] superDeclarations;
	private final Permissions execPermissions;

	public MethodInformation(TypeInformation type,
			MethodInformation superDefinition,
			Collection<MethodInformation> superDeclarations,
			Permissions execPermissions) {
		this.type = type;
		this.execPermissions = execPermissions;
		this.superDefinition = superDefinition;
		this.superDeclarations = new MethodInformation[superDeclarations.size()];
		superDeclarations.toArray(this.superDeclarations);
	}

	public TypeInformation getType() {
		return type;
	}

	public Permissions getExecPermissions() {
		return execPermissions;
	}

	public MethodInformation getSuperDefinition() {
		return superDefinition;
	}

	public MethodInformation[] getSuperDeclarations() {
		return superDeclarations.clone();
	}

}
