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
