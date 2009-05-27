package com.google.code.annatasha.validator.internal.build;

final class MarkedCast implements IMarkedValue {

	private final IMarkerFactory factory;
	public final MarkedString destinationType;
	public final MarkedExpression operand;
	
	public MarkedCast(IMarkerFactory factory, MarkedString destinationType, MarkedExpression operand) {
		this.factory = factory;
		this.destinationType = destinationType;
		this.operand = operand;
	}

	public IMarkerFactory getMarkerFactory() {
		return factory;
	}

}
