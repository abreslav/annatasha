package com.google.code.annatasha.validator.internal.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public final class Permissions implements Iterable<TypeInformation> {
	
	public final static Permissions Any = new Permissions();

	private final ArrayList<TypeInformation> types;
	private final boolean anonymous;
	
	private Permissions() {
		this.anonymous = true;
		this.types = new ArrayList<TypeInformation>();
	}

	public Permissions(Collection<TypeInformation> types) {
		this.types = new ArrayList<TypeInformation>(types);
		
		assert this.types.size() != 0;
		this.anonymous = false;
	}

	public Iterator<TypeInformation> iterator() {
		return this.types.iterator();
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	/**
	 * Validates that an these permissions allow access to an resource marker
	 * with resourcePermissions.
	 * 
	 * @param resourcePermissons
	 *            The access permissions to accessed resource. Resource is
	 *            either method call or field access.
	 * @return
	 */
	public boolean mightAccess(Permissions resourcePermissons) {
		if (isAnonymous())
			return resourcePermissons.isAnonymous();
		
		if (resourcePermissons.isAnonymous())
			return true;

		for (TypeInformation accessor : this) {
			boolean ok = false;
			for (TypeInformation resource : resourcePermissons) {
				if (accessor.getBinding().isAssignmentCompatible(resource.getBinding())) {
					ok = true;
					break;
				}
			}
			if (!ok) {
				return false;
			}
		}
		return true;
	}
	
	public static Permissions concat(final Permissions lhs, final Permissions rhs) {
		if (lhs.isAnonymous() || rhs.isAnonymous()) {
			return Any;
		}
		ArrayList<TypeInformation> info = new ArrayList<TypeInformation>(lhs.types.size() + rhs.types.size());
		info.addAll(lhs.types);
		info.addAll(rhs.types);
		return new Permissions(info);
	}

}
