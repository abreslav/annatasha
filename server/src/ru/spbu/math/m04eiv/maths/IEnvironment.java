package ru.spbu.math.m04eiv.maths;

import ru.spbu.math.m04eiv.maths.matrix.MatrixPool;
import ru.spbu.math.m04eiv.maths.processor.WorkersManager;

public interface IEnvironment {

	public abstract MatrixPool getPool();

	public abstract WorkersManager getManager();

}