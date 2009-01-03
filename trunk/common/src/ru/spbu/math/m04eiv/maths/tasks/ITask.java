package ru.spbu.math.m04eiv.maths.tasks;

import ru.spbu.math.m04eiv.maths.matrix.IMatrixReader;
import ru.spbu.math.m04eiv.maths.matrix.IMatrixWriter;

import com.google.code.annatasha.annotations.Method;

public interface ITask {
	@Method.ExecPermissions(IResourceManager.class)
	public abstract boolean tryFetchResources();

	@Method.ExecPermissions(IResourceManager.class)
	public abstract void releaseResources();
	
	@Method.ExecPermissions({IMatrixReader.class, IMatrixWriter.class})
	public abstract void execute();
	
	@Method.ExecPermissions(ITaskManager.class)
	public abstract void interrupt();
}
