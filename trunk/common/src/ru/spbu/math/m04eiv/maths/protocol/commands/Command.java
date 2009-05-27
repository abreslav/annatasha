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

package ru.spbu.math.m04eiv.maths.protocol.commands;

import com.google.code.annatasha.annotations.ThreadMarker;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

public abstract class Command {

	@ThreadMarker
	public interface TConstructor {}
	
	@ThreadMarker
	public interface TVisitor {}
	
	@ThreadMarker
	public interface TReader extends TVisitor {}

	@ThreadMarker
	public interface TWriter {}
	
	@ExecPermissions(TVisitor.class)
	public abstract void acceptVisitor(CommandsVisitor visitor);
	
}
