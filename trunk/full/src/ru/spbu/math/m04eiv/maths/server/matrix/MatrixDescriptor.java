package ru.spbu.math.m04eiv.maths.server.matrix;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.spbu.math.m04eiv.maths.common.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.common.matrix.TMatrixReader;
import ru.spbu.math.m04eiv.maths.common.matrix.TMatrixWriter;
import ru.spbu.math.m04eiv.maths.common.protocol.Status;
import ru.spbu.math.m04eiv.maths.tasks.TResourceManager;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public final class MatrixDescriptor {

	private volatile Status status = Status.Processing;

	private final Matrix matrix = new Matrix(0, 0);

	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

	@ExecPermissions( { TMatrixReader.class, TMatrixWriter.class })
	public Matrix getMatrix() {
		return matrix;
	}

	@ExecPermissions( { TMatrixReader.class, TMatrixWriter.class })
	public void setStatus(Status status) {
		this.status = status;
	}

	@ExecPermissions( { TMatrixReader.class, TResourceManager.class })
	public Status getStatus() {
		return status;
	}

	@ExecPermissions(TResourceManager.class)
	public ReadWriteLock getLock() {
		return rwLock;
	}

}
