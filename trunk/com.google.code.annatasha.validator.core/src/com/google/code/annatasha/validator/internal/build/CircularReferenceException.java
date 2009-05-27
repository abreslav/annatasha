package com.google.code.annatasha.validator.internal.build;

final class CircularReferenceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3800936195348418760L;
	
	public final SymbolInformation symbolInformation;

	public CircularReferenceException(SymbolInformation symbolInformation) {
		this.symbolInformation = symbolInformation;
	}

}
