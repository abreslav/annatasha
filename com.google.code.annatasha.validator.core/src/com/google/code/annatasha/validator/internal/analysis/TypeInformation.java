package com.google.code.annatasha.validator.internal.analysis;

import java.util.HashMap;

public final class TypeInformation {

	public final static TypeInformation Anonymous = new TypeInformation();

	public static final int K_INTERFACE = 0;
	public static final int K_CLASS = 1;
	public static final int K_ENUM = 2;
	public static final int K_ANNOTATION = 3;

	private static final int K_KIND_MASK = 3;

	public static final int F_RUNNABLE = 4;
	public static final int F_MARKER = F_RUNNABLE + 8;

	private final int flags;
	private final boolean anonymous;
	private final String fullyQualifiedName;

	private final TypeInformation superClass;
	private final TypeInformation[] superInterfaces;

	private HashMap<TypeInformation, Boolean> assignmentMap;

	private Permissions execPermissions;

	private TypeInformation() {
		this.anonymous = true;

		this.flags = F_MARKER | K_INTERFACE;
		this.superClass = null;
		this.superInterfaces = new TypeInformation[0];
		this.fullyQualifiedName = "<anonymous marker>";
		this.execPermissions = Permissions.Anonymous;
	}

	public TypeInformation(String fullyQualifiedName, int flags,
			TypeInformation superClass, TypeInformation[] superInterfaces, Permissions execPermissions) {
		this.anonymous = false;
		this.fullyQualifiedName = fullyQualifiedName;

		this.flags = flags;
		this.superClass = superClass;
		this.superInterfaces = superInterfaces.clone();
		this.execPermissions = execPermissions;
	}

	public boolean isMarkerAssignmentCompatible(TypeInformation to) {
		assert isMarker() && (to != null && to.isMarker()) : "Only markers must be checked for assignment compatibility";

		if (anonymous)
			return true;

		if (this.equals(to))
			return true;

		Boolean result = assignmentMap.get(to);
		if (result == null) {
			result = false;
			for (TypeInformation superInterface : superInterfaces) {
				if (superInterface.isMarkerAssignmentCompatible(to)) {
					result = true;
					break;
				}
			}
			assignmentMap.put(to, result);
		}
		return result;
	}

	public Permissions getExecPermissions() {
		assert !isMarker() : "Marker must not be queried about ExecPermissions";
		return execPermissions;
	}

	public TypeInformation getSuperClass() {
		return superClass;
	}

	public TypeInformation[] getSuperInterfaces() {
		return superInterfaces;
	}

	public boolean isClass() {
		return isClass(flags);
	}

	public boolean isInterface() {
		return isInterface(flags);
	}

	public boolean isRunnable() {
		return (flags & F_RUNNABLE) == F_RUNNABLE;
	}

	public boolean isMarker() {
		return (flags & F_MARKER) == F_MARKER;
	}

	public static boolean isInterface(int flags) {
		return isOfKind(flags, K_INTERFACE);
	}

	public static boolean isClass(int flags) {
		return isOfKind(flags, K_CLASS);
	}

	private static boolean isOfKind(int flags, int kind) {
		return (flags & K_KIND_MASK) == kind;
	}

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}
}
