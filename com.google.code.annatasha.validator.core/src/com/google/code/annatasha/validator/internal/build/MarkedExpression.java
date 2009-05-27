/**
 * 
 */
package com.google.code.annatasha.validator.internal.build;

final class MarkedExpression implements IMarkedValue {

	enum Kind {
		LOCAL_VARIABLE, METHOD_FORMAL_ARGUMENT, METHOD_REAL_ARGUMENT, FIELD
	}

	private final IMarkerFactory factory;

	private final Kind kind;
	private final String key;
	private final int argumentNumber;

	private MarkedExpression(IMarkerFactory factory, Kind kind, String key, int argumentNumber) {
		this.factory = factory;
		this.kind = kind;
		this.key = key;
		this.argumentNumber = argumentNumber;
	}

	public static MarkedExpression getLocalVariable(IMarkerFactory factory) {
		return new MarkedExpression(factory, Kind.LOCAL_VARIABLE, null, -1);
	}

	public static MarkedExpression getMethodRealArgument(IMarkerFactory factory, String methodKey, int argNum) {
		return new MarkedExpression(factory, Kind.METHOD_REAL_ARGUMENT, methodKey, argNum);
	}

	public static MarkedExpression getMethodFormalArgument(IMarkerFactory factory, String formalArgumentKey) {
		return new MarkedExpression(factory, Kind.METHOD_FORMAL_ARGUMENT, formalArgumentKey, -1);
	}

	public static MarkedExpression getField(IMarkerFactory factory, String fieldKey) {
		return new MarkedExpression(factory, Kind.FIELD, fieldKey, -1);
	}

	public IMarkerFactory getMarkerFactory() {
		return factory;
	}

	public Kind getKind() {
		return kind;
	}
	
	// For FIELD only
	public String getFieldKey() {
		return key;
	}
	
	// For METHOD_FORMAL_ARGUMENT
	public String getArgumentKey() {
		return key;
	}
	
	// For METHOD_REAL_ARGUMENT only 
	public String getMethodKey() {
		return key;
	}

	public int getArgumentNumber() {
		return argumentNumber;
	}
	
}