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

package ru.spbu.math.m04eiv.maths.protocol;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ru.spbu.math.m04eiv.maths.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.tasks.ITask;
import ru.spbu.math.m04eiv.maths.tasks.ITasksFactory;
import ru.spbu.math.m04eiv.maths.tasks.ITasksProcessor;
import ru.spbu.math.m04eiv.maths.tasks.TTaskManager;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public class CommandRunner implements ICommandRunner {

	private final class CommandExecutor implements Runnable, TTaskManager {
		private final ITask t;

		private CommandExecutor(ITask t) {
			this.t = t;
		}

		@Override
		public void run() {
			processor.addTask(t);
			t.join();
		}
	}

	private Protocol protocol;

	private final ITasksProcessor processor;
	private final ITasksFactory tasksFactory;

	private final Executor executor = Executors.newSingleThreadExecutor();

	public CommandRunner(ITasksProcessor processor, ITasksFactory tasksFactory) {
		this.processor = processor;
		this.tasksFactory = tasksFactory;
	}

	@ExecPermissions(TCommandRunnerOwner.class)
	public void setProtocol(Protocol protocol) {
		assert this.protocol == null;

		this.protocol = protocol;
	}

	@Override
	public void push(Command command) {
		assert this.protocol != null;

		final ITask t = tasksFactory.createTask(protocol, command);
		if (t != null) {
			executor.execute(new CommandExecutor(t));

		}
	}

}
