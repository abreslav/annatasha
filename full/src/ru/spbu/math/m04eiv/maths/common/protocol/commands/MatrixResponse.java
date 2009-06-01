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
import ru.spbu.math.m04eiv.maths.common.protocol.Status;

public final class MatrixResponse extends Command {

	private final int uid;

	private final Status status;

	private final Matrix matrix;

	/**
	 * 
	 * @param uid
	 * @param status
	 * @param matrix
	 *            Matrix to write down to response. It's not copied!
	 */
	public MatrixResponse(int uid, Status status, Matrix matrix) {
		this.uid = uid;
		this.status = status;
		this.matrix = matrix;
	}

	public int getUid() {
		return uid;
	}

	public Status getStatus() {
		return status;
	}

	public Matrix getMatrix() {
		return matrix;
	}

	@Override
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
