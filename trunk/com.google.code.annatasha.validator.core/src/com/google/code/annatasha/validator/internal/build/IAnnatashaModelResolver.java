package com.google.code.annatasha.validator.internal.build;

import java.util.Collection;

interface IAnnatashaModelResolver extends Iterable<SymbolInformation> {

	public void setTypeInformation(String key, TypeInformation typeInformation);

	public TypeInformation getTypeInformation(String key);

	public void setFieldInformation(String key, FieldInformation result);

	public FieldInformation getFieldInformation(String key);

	public void setMethodInformation(String key, MethodInformation result);

	public MethodInformation getMethodInformation(String key);

	public boolean isUnderConstruction(String key);

	public void setUnderConstructionFlag(String key);

	public void removeUnderConstructionFlag(String key);

	public SymbolInformation getSymbolInformation(String symbol);

	public void removeSymbolInformation(String key);

	public Collection<String> getKeys();

	public void clear();

}