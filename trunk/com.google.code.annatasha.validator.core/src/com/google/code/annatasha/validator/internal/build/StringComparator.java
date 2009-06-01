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

/**
 * 
 */
package com.google.code.annatasha.validator.internal.build;

import java.util.Comparator;

public final class StringComparator implements Comparator<String> {
	public final static StringComparator INSTANCE = new StringComparator();
	
	private StringComparator() {}
	
	public int compare(String o1, String o2) {
		return o1.compareTo(o2);
	}
}