package com.google.code.annatasha.validator.internal.build;

final class MarkedAssignment implements IMarkedValue {

	private final IMarkerFactory factory;
	public final MarkedExpression left;
	public final MarkedExpression right;

	public MarkedAssignment(IMarkerFactory factory, MarkedExpression left, MarkedExpression right) {
		this.factory = factory;
		this.left = left;
		this.right = right;
	}

	public IMarkerFactory getMarkerFactory() {
		return factory;
	}

}
