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

public class SetMatrix extends Command {

	private final String name;

	private final Matrix matrix;

	/**
	 * 
	 * @param name
	 *            The name of matrix
	 * @param matrix
	 *            Matrix itself, it's not copied!
	 */
	public SetMatrix(String name, Matrix matrix) {
		this.name = name;
		this.matrix = matrix;
	}

	public SetMatrix(String name, int m, int n, int[] data) {
		this.name = name;
		matrix = new Matrix(m, n, data);
	}

	public String getName() {
		return name;
	}

	public Matrix getMatrix() {
		return matrix;
	}

	@Override
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
