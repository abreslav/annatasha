package com.google.code.annatasha.validator.internal.analysis;

public final class FieldInformation {

	private final TypeInformation type;

	private final Permissions readPermissions;
	private final Permissions writePermissions;

	public FieldInformation(TypeInformation type, Permissions readPermissions,
			Permissions writePermissions) {
		this.type = type;
		
		this.readPermissions = readPermissions;
		this.writePermissions = writePermissions;
	}
	
	public TypeInformation getType() {
		return type;
	}

	public Permissions getReadPermissions() {
		return readPermissions;
	}

	public Permissions getWritePermissions() {
		return writePermissions;
	}

}
