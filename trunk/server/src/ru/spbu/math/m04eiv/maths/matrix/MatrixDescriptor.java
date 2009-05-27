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

package ru.spbu.math.m04eiv.maths.matrix;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.spbu.math.m04eiv.maths.protocol.Status;
import ru.spbu.math.m04eiv.maths.tasks.TResourceManager;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public final class MatrixDescriptor {

	private volatile Status status = Status.Processing;

	private final Matrix matrix = new Matrix(0, 0);

	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

	@ExecPermissions( { TMatrixReader.class, TMatrixWriter.class })
	public Matrix getMatrix() {
		return matrix;
	}

	@ExecPermissions( { TMatrixReader.class, TMatrixWriter.class })
	public void setStatus(Status status) {
		this.status = status;
	}

	@ExecPermissions( { TMatrixReader.class, TResourceManager.class })
	public Status getStatus() {
		return status;
	}

	@ExecPermissions(TResourceManager.class)
	public ReadWriteLock getLock() {
		return rwLock;
	}

}
