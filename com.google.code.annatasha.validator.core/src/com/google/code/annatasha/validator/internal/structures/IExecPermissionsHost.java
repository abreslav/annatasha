package com.google.code.annatasha.validator.internal.structures;

public interface IExecPermissionsHost {

	public abstract Permissions getExecPermissions();

	public abstract boolean areExecPermissionsValid();

}