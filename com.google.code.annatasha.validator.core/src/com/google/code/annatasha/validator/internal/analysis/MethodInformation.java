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
	
	private final boolean permissionsDowncast;
	private final boolean[] threadStarter;

	public MethodInformation(TypeInformation type,
			MethodInformation superDefinition,
			Collection<MethodInformation> superDeclarations,
			Permissions execPermissions, 
			boolean permissionsDowncast,
			boolean[] threadStarter) {
		this.type = type;
		this.execPermissions = execPermissions;
		this.superDefinition = superDefinition;
		this.superDeclarations = new MethodInformation[superDeclarations.size()];
		this.permissionsDowncast = permissionsDowncast;
		this.threadStarter = threadStarter.clone();
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
	
	public boolean[] getThreadStarterFlags() {
		return threadStarter;
	}
	
	public boolean hasSuperDeclaration() {
		return superDefinition != null || superDeclarations.length != 0;
	}
	
	public boolean hasPermissionsDowncast() {
		return permissionsDowncast;
	}

}
