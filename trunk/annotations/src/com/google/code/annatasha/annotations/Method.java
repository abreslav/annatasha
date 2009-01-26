package com.google.code.annatasha.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the execution permissions to method or constructor. When applied to
 * the whole type, it's applied to every method and constructor of the type but
 * not to the inner types.
 * 
 * @author Ivan Egorov
 * 
 */
public interface Method {
	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE })
	@Inherited
	public @interface ExecPermissions {
		Class<?>[] value();
	}

}
