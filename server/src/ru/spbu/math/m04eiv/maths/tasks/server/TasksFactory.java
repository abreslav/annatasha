package ru.spbu.math.m04eiv.maths.tasks.server;

import ru.spbu.math.m04eiv.maths.matrix.MatrixPool;
import ru.spbu.math.m04eiv.maths.processor.Task;
import ru.spbu.math.m04eiv.maths.processor.WorkersManager;
import ru.spbu.math.m04eiv.maths.protocol.Protocol;
import ru.spbu.math.m04eiv.maths.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.protocol.commands.CommandsVisitor;
import ru.spbu.math.m04eiv.maths.protocol.commands.GetMatrix;
import ru.spbu.math.m04eiv.maths.protocol.commands.MatrixResponse;
import ru.spbu.math.m04eiv.maths.protocol.commands.MultiplyMatrices;
import ru.spbu.math.m04eiv.maths.protocol.commands.SetMatrix;
import ru.spbu.math.m04eiv.maths.tasks.ITasksFactory;

public final class TasksFactory implements ITasksFactory {

	private final WorkersManager manager;
	private final MatrixPool pool;

	public TasksFactory(WorkersManager manager, MatrixPool pool) {
		super();
		this.manager = manager;
		this.pool = pool;
	}

	public Task createTask(Protocol protocol, Command command) {
		Builder b = new Builder(protocol, manager, pool);
		command.acceptVisitor(b);
		return b.getTask();
	}

	private static class Builder implements CommandsVisitor {

		private Task task;
		private final WorkersManager manager;
		private final MatrixPool pool;
		private final Protocol protocol;

		private Builder(Protocol protocol, WorkersManager manager,
				MatrixPool pool) {
			this.protocol = protocol;
			this.manager = manager;
			this.pool = pool;
			this.task = null;
		}

		public Task getTask() {
			return task;
		}

		@Override
		public void visit(GetMatrix command) {
			task = new GetMatrixTask(manager, pool, protocol, command);
		}

		@Override
		public void visit(SetMatrix command) {
			task = new SetMatrixTask(manager, pool, command);
		}

		@Override
		public void visit(MultiplyMatrices command) {
			task = new MuliplyMatricesTask(manager, pool, command);
		}

		@Override
		public void visit(MatrixResponse matrixResponse) {
		}

	}

}
