package com.google.code.annatasha.validator.internal.build;

interface ISourcePolicy {

	/**
	 * true if source has been updated since last synchronisation operation
	 * 
	 * @return
	 */
	boolean hasChanged();

	/**
	 * Sets new synchronisation point, flushes {@link #hasChanged()} flag
	 */
	void markSynchronised();

	/**
	 * Returns marker factory for specified symbol.
	 * 
	 * @param symbolInformation
	 *            symbol which has this object as source policy
	 */
	IMarkerFactory getMarkerFactory(SymbolInformation symbolInformation);

}
