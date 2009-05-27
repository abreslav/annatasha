package com.google.code.annatasha.validator.internal.build;

final class FieldInformation extends SymbolInformation {

	// Obtained during local lookup
	public MarkedString type;

	public Permissions readPermissions;
	public Permissions writePermissions;

	public MarkedBoolean threadStarter;
	
	// Obtained during global lookup
	// NONE

	public FieldInformation(IAnnatashaModelResolver resolver, String key, ISourcePolicy sourcePolicy) {
		super(resolver, key, sourcePolicy);
	}

	public MarkedString getType() {
		return type;
	}

	public Permissions getReadPermissions() {
		return readPermissions;
	}

	public Permissions getWritePermissions() {
		return writePermissions;
	}

	public boolean isThreadStarter() {
		return threadStarter.value;
	}

	@Override
	void acceptVisitor(SymbolVisitor visitor) {
		visitor.visit(this);
	}

}
