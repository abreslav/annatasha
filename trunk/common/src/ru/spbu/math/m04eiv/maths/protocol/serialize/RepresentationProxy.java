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

package ru.spbu.math.m04eiv.maths.protocol.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.code.annatasha.annotations.ThreadMarker;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

public interface RepresentationProxy<T> {
	
	@ThreadMarker
	interface Reader {}
	
	@ThreadMarker
	interface Writer {}
	
	@ExecPermissions(Writer.class)
	void writeToStream(T object, OutputStream stream) throws IOException;
	
	@ExecPermissions(Reader.class)
	T readFromStream(InputStream stream) throws IOException;

}
