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

package ru.spbu.math.m04eiv.maths.tasks;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

import ru.spbu.math.m04eiv.maths.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.matrix.MatrixDescriptor;
import ru.spbu.math.m04eiv.maths.matrix.MatrixPool;
import ru.spbu.math.m04eiv.maths.matrix.MatrixPool.Lock;
import ru.spbu.math.m04eiv.maths.processor.Task;
import ru.spbu.math.m04eiv.maths.processor.WorkersManager;
import ru.spbu.math.m04eiv.maths.protocol.Protocol;
import ru.spbu.math.m04eiv.maths.protocol.commands.GetMatrix;
import ru.spbu.math.m04eiv.maths.protocol.commands.MatrixResponse;

final class GetMatrixTask extends Task {

	private final MatrixPool pool;
	private final Matrix matrix;

	private final Protocol protocol;

	private final String[] readDescriptors;
	private final static String[] writeDescriptors = new String[0];

	private Lock lock;
//	private volatile Status status;
	private final GetMatrix command;

	public GetMatrixTask(WorkersManager man, MatrixPool pool,
			Protocol protocol, GetMatrix command) {
		super(man, "GET " + command.getName());
		this.pool = pool;
		this.protocol = protocol;
		this.command = command;
		this.matrix = new Matrix(0, 0);

		readDescriptors = new String[] { command.getName() };
	}

	public final Matrix getMatrix() {
		return matrix;
	}

	@Override
	@ExecPermissions(Task.TExecutor.class)
	public void execute() {
		final MatrixDescriptor readDescriptor = lock.getReadDescriptor(0);

		protocol.writeCommand(new MatrixResponse(command.getUid(),
				readDescriptor.getStatus(), readDescriptor.getMatrix()));

		done();
	}

	@Override
	public void interrupt() {
	}

	@Override
	public void releaseResources() {
		pool.releaseLock(lock);
	}

	@Override
	public boolean tryFetchResources() {
		lock = pool.tryAcquireLock(readDescriptors, writeDescriptors);
		return lock.isAcquired();
	}

//	public Status getStatus() {
//		return status;
//	}

}
