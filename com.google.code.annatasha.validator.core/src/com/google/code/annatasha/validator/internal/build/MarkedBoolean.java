package com.google.code.annatasha.validator.internal.build;

final class MarkedBoolean implements IMarkedValue {
	
	private final IMarkerFactory factory;
	public final boolean value;

	public MarkedBoolean(IMarkerFactory factory, boolean value) {
		this.factory = factory;
		this.value = value;
	}

	public IMarkerFactory getMarkerFactory() {
		return factory;
	}

}
