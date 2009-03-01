package com.google.code.annatasha.validator.internal.analysis;

public interface IExecPermissionsHost {

	public abstract Permissions getExecPermissions();

	public abstract boolean areExecPermissionsValid();

}