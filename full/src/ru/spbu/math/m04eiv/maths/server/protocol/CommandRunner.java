package ru.spbu.math.m04eiv.maths.server.protocol;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

import ru.spbu.math.m04eiv.maths.common.protocol.ICommandRunner;
import ru.spbu.math.m04eiv.maths.common.protocol.Protocol;
import ru.spbu.math.m04eiv.maths.common.protocol.TCommandsTasksFactory;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.common.tasks.ITask;
import ru.spbu.math.m04eiv.maths.common.tasks.ITasksFactory;
import ru.spbu.math.m04eiv.maths.server.tasks.ITasksProcessor;

public class CommandRunner implements ICommandRunner {

	private final class CommandExecutor implements Runnable, TTaskProcessor {
		private final ITask t;

		private CommandExecutor(ITask t) {
			this.t = t;
		}

		@Override
		@ExecPermissions(TTaskProcessor.class)
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

	public void setProtocol(Protocol protocol) {
		assert this.protocol == null;

		this.protocol = protocol;
	}

	@Override
	@ExecPermissions(TCommandsTasksFactory.class)
	public void push(Command command) {
		assert this.protocol != null;

		final ITask t = tasksFactory.createTask(protocol, command);
		if (t != null) {
			executor.execute(new CommandExecutor(t));

		}
	}

}
