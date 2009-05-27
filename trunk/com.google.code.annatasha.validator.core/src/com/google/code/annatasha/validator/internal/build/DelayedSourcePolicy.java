package com.google.code.annatasha.validator.internal.build;

final class DelayedSourcePolicy implements ISourcePolicy {

	private ISourcePolicy destination;

	public void markSynchronised() {
		if (destination != null) {
			destination.markSynchronised();
		}
	}

	public boolean hasChanged() {
		if (destination != null) {
			return destination.hasChanged();
		}
		return false;
	}

	void setSourcePolicy(ISourcePolicy destination) {
		// if (this.destination == null) {
		this.destination = destination;
		// } else {
		// throw new UnsupportedOperationException(
		// "destination is assignable only once");
		// }
	}

	boolean isAssigned() {
		return destination != null;
	}

	public IMarkerFactory getMarkerFactory(SymbolInformation symbolInformation) {
		assert destination != null;
		return destination.getMarkerFactory(symbolInformation);
	}

}
