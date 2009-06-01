package ru.spbu.math.m04eiv.maths.server.tasks;

import ru.spbu.math.m04eiv.maths.common.protocol.Protocol;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.CommandsVisitor;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.GetMatrix;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.MatrixResponse;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.MultiplyMatrices;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.SetMatrix;
import ru.spbu.math.m04eiv.maths.server.matrix.MatrixPool;
import ru.spbu.math.m04eiv.maths.server.processor.Task;
import ru.spbu.math.m04eiv.maths.server.processor.WorkersManager;
import ru.spbu.math.m04eiv.maths.tasks.ITasksFactory;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public final class TasksFactory implements ITasksFactory {

	private final WorkersManager manager;
	private final MatrixPool pool;

	public TasksFactory(WorkersManager manager, MatrixPool pool) {
		super();
		this.manager = manager;
		this.pool = pool;
	}

	@ExecPermissions(TTaskCreator.class)
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
