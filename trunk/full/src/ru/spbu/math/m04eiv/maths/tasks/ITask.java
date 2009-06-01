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

import ru.spbu.math.m04eiv.maths.common.matrix.TMatrixReader;
import ru.spbu.math.m04eiv.maths.common.matrix.TMatrixWriter;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.Command;

import com.google.code.annatasha.annotations.Method;

public interface ITask {
	@Method.ExecPermissions(TResourceManager.class)
	public abstract boolean tryFetchResources();

	@Method.ExecPermissions(TResourceManager.class)
	public abstract void releaseResources();

	@Method.ExecPermissions( { TMatrixReader.class, TMatrixWriter.class,
			Command.TWriter.class })
	public abstract void execute();

	@Method.ExecPermissions(TTaskManager.class)
	public abstract void interrupt();

	@Method.ExecPermissions(TTaskManager.class)
	public abstract void join();

}
