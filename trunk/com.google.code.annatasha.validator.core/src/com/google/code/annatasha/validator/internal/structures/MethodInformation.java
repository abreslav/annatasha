/**
 * 
 */
package com.google.code.annatasha.validator.internal.structures;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Ivan Egorov
 * 
 */
public final class MethodInformation implements IExecPermissionsHost {
	
	private final TypeInformation type;
	private final MethodInformation superDefinition;
	private final MethodInformation[] superDeclarations;
	private final Permissions execPermissions;
	
	private ArrayList<Integer> threadStarters;
	private boolean execPermissionsValid;
	private boolean inheritedExecPermissionsValid;
	private final boolean entryPoint;
	private final boolean inheritedFromEntryPoint;
	private boolean threadStartersValid;
	private boolean threadStarter;

	public MethodInformation(TypeInformation type,
			MethodInformation superDefinition,
			Collection<MethodInformation> superDeclarations,
			Permissions execPermissions,
			boolean execPermissionsValid,
			boolean inheritedExecPermissionsValid,
			boolean entryPoint,
			boolean inheritedFromEntryPoint,
			boolean threadStarter,
			ArrayList<Integer> threadStarters, boolean areThreadStartersValid) {
		this.type = type;
		this.execPermissions = execPermissions;
		this.execPermissionsValid = execPermissionsValid;
		this.inheritedExecPermissionsValid = inheritedExecPermissionsValid;
		this.entryPoint = entryPoint;
		this.inheritedFromEntryPoint = inheritedFromEntryPoint;
		this.threadStarter = threadStarter;
		this.threadStarters = threadStarters;
		this.threadStartersValid = areThreadStartersValid;

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
	
	public boolean hasSuperDeclaration() {
		return superDefinition != null || superDeclarations.length != 0;
	}
	
	// New interface
	public boolean isEntryPoint() {
		return entryPoint;
	}

	public boolean isInheritedFromEntryPoint() {
		return inheritedFromEntryPoint;
	}

	public boolean areExecPermissionsValid() {
		return execPermissionsValid;
	}

	public boolean areInheritedExecPermissionsValid() {
		return inheritedExecPermissionsValid;
	}

	public ArrayList<Integer> getThreadStarterParameters() {
		return threadStarters;
	}
	
	public boolean isThreadStarter() {
		return threadStarter;
	}
	
	public boolean areThreadStartersValid() {
		return threadStartersValid;
	}
	
}
