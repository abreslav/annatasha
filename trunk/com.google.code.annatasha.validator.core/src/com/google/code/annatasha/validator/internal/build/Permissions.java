package com.google.code.annatasha.validator.internal.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

final class Permissions implements Iterable<MarkedString> {

	public final static Permissions Any = new Permissions();

	private final ArrayList<MarkedString> types;
	private final boolean anonymous;

	private Permissions() {
		this.anonymous = true;
		this.types = new ArrayList<MarkedString>();
	}

	public Permissions(Collection<MarkedString> types) {
		this.types = new ArrayList<MarkedString>(types);

		assert this.types.size() != 0;
		this.anonymous = false;
	}

	public Iterator<MarkedString> iterator() {
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
//		if (isAnonymous())
//			return resourcePermissons.isAnonymous();
//
//		if (resourcePermissons.isAnonymous())
//			return true;
//
//		for (TypeInformation accessor : this) {
//			boolean ok = false;
//			for (TypeInformation resource : resourcePermissons) {
//				if (accessor.getBinding().isAssignmentCompatible(
//						resource.getBinding())) {
//					ok = true;
//					break;
//				}
//			}
//			if (!ok) {
//				return false;
//			}
//		}
//		return true;
		throw new UnsupportedOperationException("Not implemented");
	}

	public static Permissions concat(final Permissions lhs,
			final Permissions rhs) {
//		if (lhs.isAnonymous() || rhs.isAnonymous()) {
//			return Any;
//		}
//		ArrayList<TypeInformation> info = new ArrayList<TypeInformation>(
//				lhs.types.size() + rhs.types.size());
//		info.addAll(lhs.types);
//		info.addAll(rhs.types);
//		return new Permissions(info);
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public String toString() {
		return anonymous ? "<anonymous>" : (types == null ? "null" : types
				.toString());
	}
	
	
	public static boolean equal(Permissions lhs, Permissions rhs) {
		if (lhs == null || rhs == null) 
			return false;
		
		if (lhs == rhs) 
			return true;
		
		if (lhs.anonymous && rhs.anonymous)
			return true;
		
		if (lhs.anonymous || rhs.anonymous)
			return false;
		
		if (lhs.types.size() != rhs.types.size()) 
			return false;
		
		String[] lhsKeys = new String[lhs.types.size()];
		String[] rhsKeys = new String[rhs.types.size()];
		
		for (int i = 0; i < lhsKeys.length; ++i) {
			lhsKeys[i] = lhs.types.get(i).value;
			rhsKeys[i] = rhs.types.get(i).value;
		}
		
		Arrays.sort(lhsKeys, StringComparator.INSTANCE);
		Arrays.sort(rhsKeys, StringComparator.INSTANCE);
		
		for (int i = 0; i < lhsKeys.length; ++i) {
			if (!lhsKeys[i].equals(rhsKeys[i])) {
				return false;
			}
		}
		
		return true;
	}

}
