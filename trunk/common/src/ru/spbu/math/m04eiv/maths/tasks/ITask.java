package ru.spbu.math.m04eiv.maths.tasks;

import ru.spbu.math.m04eiv.maths.matrix.TMatrixReader;
import ru.spbu.math.m04eiv.maths.matrix.TMatrixWriter;
import ru.spbu.math.m04eiv.maths.protocol.commands.Command;

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
