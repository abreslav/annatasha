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

import com.google.code.annatasha.annotations.Field.ReadPermissions;
import com.google.code.annatasha.annotations.Field.WritePermissions;
import com.google.code.annatasha.annotations.Method.ExecPermissions;


public final class MultiplyMatrices extends Command {

	@ReadPermissions(Command.TReader.class)
	@WritePermissions(Command.TConstructor.class)
	private final String lhsName;
	
	@ReadPermissions(Command.TReader.class)
	@WritePermissions(Command.TConstructor.class)
	private final String rhsName;

	@ReadPermissions(Command.TReader.class)
	@WritePermissions(Command.TConstructor.class)
	private final String destName;

	@ExecPermissions(Command.TConstructor.class)
	public MultiplyMatrices(String lhsName, String rhsName, String destName) {
		this.lhsName = lhsName;
		this.rhsName = rhsName;
		this.destName = destName;
	}

	@ExecPermissions(Command.TReader.class)
	public String[] getNames() {
		return new String[] { lhsName, rhsName, destName };
	}

	@Override
	@ExecPermissions(TVisitor.class)
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
