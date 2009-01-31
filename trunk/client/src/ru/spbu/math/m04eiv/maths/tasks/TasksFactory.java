package ru.spbu.math.m04eiv.maths.tasks;

import ru.spbu.math.m04eiv.maths.protocol.Protocol;
import ru.spbu.math.m04eiv.maths.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.protocol.commands.CommandsVisitor;
import ru.spbu.math.m04eiv.maths.protocol.commands.GetMatrix;
import ru.spbu.math.m04eiv.maths.protocol.commands.MatrixResponse;
import ru.spbu.math.m04eiv.maths.protocol.commands.MultiplyMatrices;
import ru.spbu.math.m04eiv.maths.protocol.commands.SetMatrix;

public final class TasksFactory implements ITasksFactory {

	private final static TasksFactory INSTANCE = new TasksFactory();

	public TasksFactory() {
	}

	public ITask createTask(Protocol protocol, Command command) {
		Builder b = new Builder(protocol);
		command.acceptVisitor(b);
		return b.getTask();
	}

	private static class Builder implements CommandsVisitor {

		private ITask task;
		private final Protocol protocol;

		private Builder(Protocol protocol) {
			this.protocol = protocol;
			this.task = null;
		}

		public ITask getTask() {
			return task;
		}

		@Override
		public void visit(GetMatrix command) {
			task = new QueryTask(protocol, command);
		}

		@Override
		public void visit(SetMatrix command) {
			task = new QueryTask(protocol, command);
		}

		@Override
		public void visit(MultiplyMatrices command) {
			task = new QueryTask(protocol, command);
		}

		@Override
		public void visit(MatrixResponse matrixResponse) {
			task = new MatrixResponseTask(matrixResponse);
		}

	}

	public static ITasksFactory getInstance() {
		return INSTANCE;
	}

}
