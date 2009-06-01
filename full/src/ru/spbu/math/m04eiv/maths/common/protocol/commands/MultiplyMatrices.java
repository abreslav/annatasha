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

package ru.spbu.math.m04eiv.maths.common.protocol.commands;



public final class MultiplyMatrices extends Command {

	private final String lhsName;
	
	private final String rhsName;

	private final String destName;

	public MultiplyMatrices(String lhsName, String rhsName, String destName) {
		this.lhsName = lhsName;
		this.rhsName = rhsName;
		this.destName = destName;
	}

	public String[] getNames() {
		return new String[] { lhsName, rhsName, destName };
	}

	@Override
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
