/**
 * 
 */
package com.google.code.annatasha.validator.internal.build;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.IMethodBinding;

/**
 * @author Ivan Egorov
 * 
 */
final class MethodInformation extends SymbolInformation implements
		IExecPermissionsHost {

	// Obtained during local lookup
	public IMethodBinding binding;

	public MarkedString name;
	public String declaringType;
	public boolean isStatic;
	public boolean isConstructor;

	public MarkedString returnType;
	public Permissions execPermissions;
	// The set of keys of thread starter parameters
	public ArrayList<Integer> threadStarters = new ArrayList<Integer>();
	public boolean entryPoint;
	public HashSet<String> referencedBindings;

	// Obtained during global lookup
	public MarkedString superDefinition;
	public ArrayList<MarkedString> superDeclarations = new ArrayList<MarkedString>();

	boolean inheritedFromEntryPoint;

	public MethodInformation(IAnnatashaModelResolver resolver, String key,
			ISourcePolicy sourcePolicy) {
		super(resolver, key, sourcePolicy);
	}

	// public MethodInformation(
	// String key,
	// ISourcePolicy sourcePolicy,
	// MarkedString returnType,
	// Permissions execPermissions,
	// boolean entryPoint,
	// ArrayList<Integer> threadStarters) {
	// super(key, sourcePolicy);
	// this.returnType = returnType;
	// this.execPermissions = execPermissions;
	// this.entryPoint = entryPoint;
	// this.threadStarters = threadStarters;
	// }

	public MarkedString getName() {
		return name;
	}

	public MarkedString getReturnType() {
		return returnType;
	}

	public Permissions getExecPermissions() {
		return execPermissions;
	}

	public boolean hasSuperDeclaration() {
		return superDefinition != null || superDeclarations.size() != 0;
	}

	// New interface
	public boolean isEntryPoint() {
		return entryPoint;
	}

	public boolean isInheritedFromEntryPoint() {
		return inheritedFromEntryPoint;
	}

	@Override
	void acceptVisitor(SymbolVisitor visitor) {
		visitor.visit(this);
	}

}
