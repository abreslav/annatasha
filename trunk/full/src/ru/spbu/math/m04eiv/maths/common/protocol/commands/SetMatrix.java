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

import ru.spbu.math.m04eiv.maths.common.matrix.Matrix;

import com.google.code.annatasha.annotations.Field.ReadPermissions;
import com.google.code.annatasha.annotations.Field.WritePermissions;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

public class SetMatrix extends Command {

	@ReadPermissions(Command.TReader.class)
	@WritePermissions(Command.TConstructor.class)
	private final String name;
	
	@ReadPermissions(Command.TReader.class)
	@WritePermissions(Command.TConstructor.class)
	private final Matrix matrix;

	/**
	 * 
	 * @param name
	 *            The name of matrix
	 * @param matrix
	 *            Matrix itself, it's not copied!
	 */
	@ExecPermissions(Command.TConstructor.class)
	public SetMatrix(String name, Matrix matrix) {
		this.name = name;
		this.matrix = matrix;
	}

	@ExecPermissions(Command.TConstructor.class)
	public SetMatrix(String name, int m, int n, int[] data) {
		this.name = name;
		matrix = new Matrix(m, n, data);
	}

	@ExecPermissions(Command.TReader.class)
	public String getName() {
		return name;
	}
	
	@ExecPermissions(Command.TReader.class)
	public Matrix getMatrix() {
		return matrix;
	}

	@Override
	@ExecPermissions(TVisitor.class)
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
