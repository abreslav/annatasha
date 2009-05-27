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

import ru.spbu.math.m04eiv.maths.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.protocol.Status;

import com.google.code.annatasha.annotations.Field.ReadPermissions;
import com.google.code.annatasha.annotations.Field.WritePermissions;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

public final class MatrixResponse extends Command {

	@ReadPermissions(Command.TReader.class)
	@WritePermissions(Command.TConstructor.class)
	private final int uid;

	@ReadPermissions(Command.TReader.class)
	@WritePermissions(Command.TConstructor.class)
	private final Status status;

	@ReadPermissions(Command.TReader.class)
	@WritePermissions(Command.TConstructor.class)
	private final Matrix matrix;

	/**
	 * 
	 * @param uid
	 * @param status
	 * @param matrix
	 *            Matrix to write down to response. It's not copied!
	 */
	@ExecPermissions(Command.TConstructor.class)
	public MatrixResponse(int uid, Status status, Matrix matrix) {
		this.uid = uid;
		this.status = status;
		this.matrix = matrix;
	}

	@ExecPermissions(Command.TReader.class)
	public int getUid() {
		return uid;
	}

	@ExecPermissions(Command.TReader.class)
	public Status getStatus() {
		return status;
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
