package com.google.code.annatasha.validator.internal.analysis;

import org.eclipse.jdt.core.dom.ITypeBinding;

public final class TypeInformation {
	
	private final ITypeBinding sourceType;
	
	private boolean processing;
	
	private boolean initialized;
	private boolean valid;
	
	private boolean mFinal;

	private boolean threadMarker;
	private TypeInformation[] markers;
	
	public TypeInformation(ITypeBinding sourceType) {
		this.initialized = false;
		this.valid = false;
		this.sourceType = sourceType;
	}
	
	public void markProcessing() {
		this.processing = true;
	}
	
	public void markProcessed() {
		this.processing = false;
	}
	
	public boolean isProcessing() {
		return !initialized && processing;
	}

	public void initialize(boolean valid, boolean isFinal, boolean threadMarker, TypeInformation[] markers) {
		assert !initialized : "Type information must be not initialized";
	
		this.valid = valid;
		this.mFinal = isFinal;
		this.threadMarker = threadMarker;
		this.markers = markers;
		this.initialized = true;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public boolean isThreadMarker() {
		return threadMarker;
	}
	
	public ITypeBinding getSourceType() {
		return sourceType;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public TypeInformation[] getMarkers() {
		return markers;
	}
	
	public boolean isFinal() {
		return mFinal;
	}

}
