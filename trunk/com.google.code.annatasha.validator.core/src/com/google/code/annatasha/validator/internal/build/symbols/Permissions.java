/*******************************************************************************
 * Copyright (c) 2008, 2009 Ivan Egorov <egorich.3.04@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ivan Egorov <egorich.3.04@gmail.com>
 *******************************************************************************/

package com.google.code.annatasha.validator.internal.build.symbols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ITypeBinding;

import com.google.code.annatasha.validator.internal.build.IModelResolver;
import com.google.code.annatasha.validator.internal.build.StringComparator;
import com.google.code.annatasha.validator.internal.build.markers.MarkedString;

public final class Permissions implements Iterable<MarkedString> {

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
	
	public List<MarkedString> getClasses() {
		return types;
	}

	public static boolean mightAccess(IModelResolver resolver, Permissions callee, Permissions called) {
		if (called.isAnonymous())
			return true;
	
		if (callee.isAnonymous())
			return false;
	
		List<MarkedString> calleeClasses = callee.getClasses();
		List<MarkedString> calledClasses = called.getClasses();
	
		TypeInformation[] calleeInfo = new TypeInformation[calleeClasses.size()];
		TypeInformation[] calledInfo = new TypeInformation[calledClasses.size()];
	
		int i = 0;
		for (MarkedString s : calleeClasses) {
			TypeInformation information = resolver.getTypeInformation(s.value);
	
			if (information == null || information.getBinding() == null)
				return false;
	
			calleeInfo[i] = information;
			++i;
		}
	
		i = 0;
		for (MarkedString s : calledClasses) {
			TypeInformation information = resolver.getTypeInformation(s.value);
			if (information == null)
				return false;
	
			calledInfo[i] = information;
			++i;
		}
	
		for (TypeInformation calleeType : calleeInfo) {
			ITypeBinding calleeBinding = calleeType.getBinding();
			boolean hasCorrespondence = false;
			for (TypeInformation calledType : calledInfo) {
				ITypeBinding calledBinding = calledType.getBinding();
	
				if (calledBinding != null
						&& calleeBinding.isAssignmentCompatible(calledBinding)) {
					hasCorrespondence = true;
					break;
				}
			}
			if (!hasCorrespondence)
				return false;
		}
	
		return true;
	}

}
