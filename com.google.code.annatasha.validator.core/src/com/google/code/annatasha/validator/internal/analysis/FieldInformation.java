package com.google.code.annatasha.validator.internal.analysis;

public final class FieldInformation {

	private final TypeInformation type;

	private final Permissions readPermissions;
	private final Permissions writePermissions;

	private boolean readPermissionsValid;

	private boolean writePermissionsValid;

	public FieldInformation(TypeInformation type, Permissions readPermissions,
			boolean readPermissionsValid,
			Permissions writePermissions, boolean writePermissionsValid) {
		this.type = type;
		
		this.readPermissions = readPermissions;
		this.readPermissionsValid = readPermissionsValid;
		this.writePermissions = writePermissions;
		this.writePermissionsValid = writePermissionsValid;
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

	public boolean areReadPermissionsValid() {
		return readPermissionsValid;
	}

	public boolean areWritePermissionsValid() {
		return writePermissionsValid;
	}

}
