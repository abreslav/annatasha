/*******************************************************************************
 * Copyright (c) 2008, 2009 Ivan Egorov <egorich.3.04@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ivan Egorov <egorich.3.04@gmail.com>
 *******************************************************************************/

/**
 * 
 */
package com.google.code.annatasha.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ivan Egorov
 * 
 */
public interface Parameter {

	/**
	 * Specifies, that specific parameters' methods might be called by method
	 * implementation. Methods must be written if one of following formats:
	 * <ul>
	 * <li>
	 * <i>method-name</i> in case when method is not overloaded;</li>
	 * <li>
	 * <i>method-name(param1-type, param2-type, ..., paramN-type)</i> otherwise;
	 * </li>
	 * </ul>
	 * 
	 * The specified methods are the only that might be called by any
	 * implementation of this method or overriding one.
	 * <p>
	 * 
	 * The attribute is inherited by overriding methods. For overriding methods,
	 * attribute might be redefined with a subset of methods, comparing to
	 * inherited method.
	 * <p>
	 * 
	 * When attribute is not specified for an virtual method (in class and all
	 * its super-classes and super-interfaces), method might call any methods of
	 * parameter.
	 * <p>
	 * 
	 * When applied to an virtual method, specifies the policy for
	 * <code>this</code> reference. Application to static or non-virtual method
	 * (including all private methods) is ignored.
	 * 
	 * @author Ivan Egorov
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.PARAMETER, ElementType.METHOD })
	@Inherited
	public @interface ExecRequest {
		/**
		 * The list of methods in format:
		 * <ul>
		 * <li>
		 * <i>method-name</i> in case when method is not overloaded;</li>
		 * <li>
		 * <i>method-name(param1-type, param2-type, ..., paramN-type)</i>
		 * otherwise;</li>
		 * </ul>
		 */
		String[] value() default {};
	}

	/**
	 * Specifies, that specific parameters' fields might be accessed for read by
	 * method implementation.
	 * 
	 * The specified fields are the only that might be accessed for read by any
	 * implementation of this method or overriding one.
	 * <p>
	 * 
	 * The attribute is inherited by overriding methods. For overriding methods,
	 * attribute might be redefined with a subset of fields, comparing to
	 * inherited method.
	 * <p>
	 * 
	 * When attribute is not specified for an virtual method (in class and all
	 * its super-classes and super-interfaces), method might read any field of
	 * parameter.
	 * 
	 * When applied to an virtual method, specifies the policy for
	 * <code>this</code> reference. Application to static or non-virtual method
	 * (including all private methods) is ignored.
	 * 
	 * @author Ivan Egorov
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.PARAMETER, ElementType.METHOD })
	@Inherited
	public @interface ReadRequest {
		/**
		 * Fields' names list
		 */
		String[] value() default {};
	}

	/**
	 * Specifies, that specific parameters' fields might be accessed for write
	 * by method implementation.
	 * 
	 * The specified fields are the only that might be accessed for write by any
	 * implementation of this method or overriding one.
	 * <p>
	 * 
	 * The attribute is inherited by overriding methods. For overriding methods,
	 * attribute might be redefined with a subset of fields, comparing to
	 * inherited method.
	 * <p>
	 * 
	 * When attribute is not specified for an virtual method (in class and all
	 * its super-classes and super-interfaces), method might write any field of
	 * parameter.
	 * 
	 * When applied to an virtual method, specifies the policy for
	 * <code>this</code> reference. Application to static or non-virtual method
	 * (including all private methods) is ignored.
	 * 
	 * @author Ivan Egorov
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.PARAMETER, ElementType.METHOD })
	@Inherited
	public @interface WriteRequest {
		/**
		 * Fields' names list
		 */
		String[] value() default {};
	}

	/**
	 * Shortcut for <code>@ExecRequest @ReadRequest @WriteRequest</code>
	 * combination, meaning, that there's no access to reference's fields or
	 * methods
	 * 
	 * @author Ivan Egorov
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.PARAMETER, ElementType.METHOD })
	@Inherited
	public @interface NoAccess {

	}

	/**
	 * Shortcut for
	 * <code>@ExecRequest({"hashCode()", "equals(Object)"}) @ReadRequest @WriteRequest</code>
	 * combination
	 * 
	 * @author Ivan Egorov
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target( { ElementType.PARAMETER, ElementType.METHOD })
	@Inherited
	public @interface Stored {

	}
	
}
