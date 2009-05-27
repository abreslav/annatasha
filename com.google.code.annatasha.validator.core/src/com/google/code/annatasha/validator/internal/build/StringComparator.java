/**
 * 
 */
package com.google.code.annatasha.validator.internal.build;

import java.util.Comparator;

final class StringComparator implements Comparator<String> {
	public final static StringComparator INSTANCE = new StringComparator();
	
	private StringComparator() {}
	
	public int compare(String o1, String o2) {
		return o1.compareTo(o2);
	}
}