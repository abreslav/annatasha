package com.google.code.annatasha.validator.internal.build;

final class MarkedString implements IMarkedValue {

	private final IMarkerFactory factory;
	public final String value;

	public MarkedString(IMarkerFactory factory, String value) {
		this.factory = factory;
		this.value = value;
	}

	public IMarkerFactory getMarkerFactory() {
		return factory;
	}
	
}
