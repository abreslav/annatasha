package com.google.code.annatasha.validator.internal.build;

import org.eclipse.jdt.core.IClassFile;

final class ClassFileSource implements ISourcePolicy {
	
	private boolean init = false;
	private long stamp = 0;
	private final IClassFile file;
	
	public ClassFileSource(IClassFile file) {
		this.file = file;
	}

	public IMarkerFactory getMarkerFactory(SymbolInformation symbolInformation) {
		return new NullMarkerFactory();
	}

	public boolean hasChanged() {
		return !init || file.getResource().getModificationStamp() > stamp;
	}

	public void markSynchronised() {
		init = true;
		stamp = file.getResource().getModificationStamp();
	}

}
