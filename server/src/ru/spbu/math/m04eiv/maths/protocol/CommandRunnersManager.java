package ru.spbu.math.m04eiv.maths.protocol;

import ru.spbu.math.m04eiv.maths.processor.WorkersManager;
import ru.spbu.math.m04eiv.maths.tasks.ITasksFactory;

public class CommandRunnersManager {

	private final WorkersManager processor;
	private final ITasksFactory tasksFactory;

	public CommandRunnersManager(WorkersManager processor, ITasksFactory tasksFactory) {
		this.processor = processor;
		this.tasksFactory = tasksFactory;
	}

	public CommandRunner newCommandRunner() {
		return new CommandRunner(processor, tasksFactory);
	}

}
