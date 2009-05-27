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

package ru.spbu.math.m04eiv.maths.processor;

import ru.spbu.math.m04eiv.maths.tasks.TTaskManager;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public interface Listener {

	public static final int DONE = -2;
	public static final int INTERRUPTED = -1;
	
	@ExecPermissions(TTaskManager.class)
	public void taskStarted(Task task, int workersCount);

	@ExecPermissions(TTaskManager.class)
	public void taskProgress(Task task, int done);

}
