package ru.spbu.math.m04eiv.maths.tasks;

import ru.spbu.math.m04eiv.maths.matrix.IMatrixReader;
import ru.spbu.math.m04eiv.maths.matrix.IMatrixWriter;

import com.google.code.annatasha.annotations.Method;

public interface ITask {
	public abstract boolean tryFetchResources();
	public abstract void releaseResources();
	
	@Method.ExecPermissions({IMatrixReader.class, IMatrixWriter.class})
	public abstract void execute();
	public abstract void interrupt();
}
