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

	/**
	 * Specifies that the method always returns the same value, called at the
	 * same object with.
	 * 
	 * Must validate that overriding method also specifies this attribute if
	 * overriden method does.
	 * 
	 * @author Ivan Egorov
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.METHOD, ElementType.CONSTRUCTOR })
	public @interface TimeImmutable {
	}

	/**
	 * The result of method is marked with specified class marker. May be used
	 * in further constraints.
	 * 
	 * @author Ivan Egorov
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.METHOD, ElementType.CONSTRUCTOR })
	@Inherited
	public @interface MarkedResult {
		Class<?> value();
	}

}
