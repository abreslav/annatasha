package com.google.code.annatasha.validator.internal.structures;

import org.eclipse.jdt.core.dom.ITypeBinding;

public final class TypeInformation implements IExecPermissionsHost {

	public final static class SuperInterfaceRecord {
		public final TypeInformation superInterface;
		public boolean typecastRestricted;

		public SuperInterfaceRecord(TypeInformation superInterface) {
			this.superInterface = superInterface;
			this.typecastRestricted = false;
		}
	}

	public final static TypeInformation Anonymous = new TypeInformation();
	private ITypeBinding binding;
	private boolean clazz;
	private boolean iface;
	private boolean annot;
	private boolean threadMarker;
	private boolean threadStarter;
	private boolean entryPoint;
	private boolean inheritedFromEntryPoint;
	private TypeInformation superClass;
	private TypeInformation[] superInterfaces;
	private TypeInformation[] superThreadMarkers;
	private Permissions execPermissions;
	private boolean execPermissionsValid;
	private boolean valid;
	private String name;
	private boolean threadStarterValid;

	private TypeInformation() {
		this(null, false, true, false, true, false, true, false, false, null,
				new TypeInformation[] {}, new TypeInformation[] {}, null, true, true);
	}

	public TypeInformation(ITypeBinding binding, boolean isClass,
			boolean isInterface, boolean isAnnotation, boolean isThreadMarker,
			boolean isThreadStarter, boolean isThreadStarterValid, 
			boolean isEntryPoint,
			boolean isInheritedFromEntryPoint, TypeInformation superClass,
			TypeInformation[] superInterfaces,
			TypeInformation[] superThreadMarkers, Permissions execPermissions,
			boolean areExecPermissionsValid, boolean valid) {
		this.binding = binding;
		this.clazz = isClass;
		this.iface = isInterface;
		this.annot = isAnnotation;
		this.threadMarker = isThreadMarker;
		this.threadStarter = isThreadStarter;
		this.threadStarterValid = isThreadStarterValid;
		this.entryPoint = isEntryPoint;
		this.inheritedFromEntryPoint = isInheritedFromEntryPoint;
		this.superClass = superClass;
		this.superInterfaces = superInterfaces.clone();
		this.superThreadMarkers = superThreadMarkers.clone();
		this.execPermissions = execPermissions;
		this.execPermissionsValid = areExecPermissionsValid;
		this.valid = valid;
		this.name = binding == null ? "<anonymous>" : binding.getQualifiedName();
	}

	// New interface
	public boolean isAnonymous() {
		return this == Anonymous;
	}

	public boolean isThreadMarker() {
		return threadMarker;
	}

	public boolean isEntryPoint() {
		return entryPoint;
	}

	public boolean isThreadStarter() {
		return threadStarter;
	}

	/**
	 * @return
	 */
	public TypeInformation[] getSuperThreadMarkers() {
		return superThreadMarkers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.analysis.IExecPermissionsHost
	 * #getExecPermissions()
	 */
	public Permissions getExecPermissions() {
		return execPermissions;
	}

	public boolean isInheritedFromEntryPoint() {
		return inheritedFromEntryPoint;
	}

	public boolean isValid() {
		return valid;
	}

	public TypeInformation getSuperClass() {
		return superClass;
	}

	public ITypeBinding getBinding() {
		return binding;
	}

	public TypeInformation[] getSuperInterfaces() {
		return superInterfaces;
	}

	public String getName() {
		return name;
	}

	public boolean isInterface() {
		return iface;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.analysis.IExecPermissionsHost
	 * #areExecPermissionsValid()
	 */
	public boolean areExecPermissionsValid() {
		return execPermissionsValid;
	}
	
	public boolean isThreadStarterValid() {
		return threadStarterValid;
	}
	
	public boolean isClass() {
		return clazz;
	}
	
	public boolean isAnnotation() {
		return annot;
	}
	
	@Override
	public String toString() {
		return binding.getQualifiedName();
	}

}
