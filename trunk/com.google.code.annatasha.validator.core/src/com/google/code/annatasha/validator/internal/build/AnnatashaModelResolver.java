package com.google.code.annatasha.validator.internal.build;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class AnnatashaModelResolver implements IAnnatashaModelResolver {
	public Map<String, SymbolInformation> resolved;
	public Set<String> underConstruction;

	public AnnatashaModelResolver() {
		resolved = new HashMap<String, SymbolInformation>();
		underConstruction = new HashSet<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #getTypeInformation(java.lang.String)
	 */
	public TypeInformation getTypeInformation(final String key) {
		try {
			return (TypeInformation) resolved.get(key);
		} catch (ClassCastException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #setTypeInformation(java.lang.String,
	 * com.google.code.annatasha.validator.internal.structures.TypeInformation)
	 */
	public void setTypeInformation(final String key,
			final TypeInformation typeInformation) {
		resolved.put(key, typeInformation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #removeUnderConstruction(java.lang.String)
	 */
	public void removeUnderConstructionFlag(String key) {
		underConstruction.remove(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #setUnderConstruction(java.lang.String)
	 */
	public void setUnderConstructionFlag(String key) {
		underConstruction.add(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #isUnderConstruction(java.lang.String)
	 */
	public boolean isUnderConstruction(String key) {
		return underConstruction.contains(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #setFieldInforamtion(java.lang.String,
	 * com.google.code.annatasha.validator.internal.structures.FieldInformation)
	 */
	public void setFieldInformation(String key, FieldInformation result) {
		resolved.put(key, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #getFieldInformation(java.lang.String)
	 */
	public FieldInformation getFieldInformation(String key) {
		try {
			return (FieldInformation) resolved.get(key);
		} catch (ClassCastException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #getMethodInformation(java.lang.String)
	 */
	public MethodInformation getMethodInformation(String key) {
		try {
			return (MethodInformation) resolved.get(key);
		} catch (ClassCastException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.annatasha.validator.internal.build.IAnnatashaModelResolver
	 * #setMethodInformation(java.lang.String,
	 * com.google.code.annatasha.validator
	 * .internal.structures.MethodInformation)
	 */
	public void setMethodInformation(String key, MethodInformation result) {
		resolved.put(key, result);
	}

	public void removeInformation(String key) {
		resolved.remove(key);
	}

	public SymbolInformation getSymbolInformation(String key) {
		return resolved.get(key);
	}

	public void removeSymbolInformation(String key) {
		resolved.remove(key);
	}

	public Iterator<SymbolInformation> iterator() {
		return resolved.values().iterator();
	}

	public Collection<String> getKeys() {
		return resolved.keySet();
	}

	public void clear() {
		resolved.clear();
		this.underConstruction.clear();
	}

}