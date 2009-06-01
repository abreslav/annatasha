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


public class GetMatrix extends Command {

	private final int uid;

	private final String name;

	public GetMatrix(int uid, String name) {
		this.uid = uid;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getUid() {
		return uid;
	}

	@Override
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
