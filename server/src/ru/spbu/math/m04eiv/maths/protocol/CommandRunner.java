package ru.spbu.math.m04eiv.maths.protocol;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ru.spbu.math.m04eiv.maths.processor.WorkersManager;
import ru.spbu.math.m04eiv.maths.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.tasks.ITask;
import ru.spbu.math.m04eiv.maths.tasks.ITaskManager;
import ru.spbu.math.m04eiv.maths.tasks.ITasksFactory;

public class CommandRunner implements ICommandRunner {

	private final class CommandExecutor implements Runnable, ITaskManager {
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

	private final WorkersManager processor;
	private final ITasksFactory tasksFactory;

	private final Executor executor = Executors.newSingleThreadExecutor();

	public CommandRunner(WorkersManager processor, ITasksFactory tasksFactory) {
		this.processor = processor;
		this.tasksFactory = tasksFactory;
	}

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
