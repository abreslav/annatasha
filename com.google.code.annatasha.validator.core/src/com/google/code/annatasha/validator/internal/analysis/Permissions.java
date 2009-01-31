package com.google.code.annatasha.validator.internal.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public final class Permissions implements Iterable<TypeInformation> {
	
	public final static Permissions Anonymous = new Permissions();

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

		for (TypeInformation accessor : this) {
			boolean ok = false;
			for (TypeInformation resource : resourcePermissons) {
				if (accessor.isMarkerAssignmentCompatible(resource)) {
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

}
