package ru.spbu.math.m04eiv.maths.matrix;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.spbu.math.m04eiv.maths.protocol.Status;
import ru.spbu.math.m04eiv.maths.tasks.IResourceManager;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public final class MatrixDescriptor {

	private volatile Status status = Status.Processing;

	private final Matrix matrix = new Matrix(0, 0);

	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

	@ExecPermissions( { IMatrixReader.class, IMatrixWriter.class })
	public Matrix getMatrix() {
		return matrix;
	}

	@ExecPermissions( { IMatrixReader.class, IMatrixWriter.class })
	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	@ExecPermissions( IResourceManager.class )
	public ReadWriteLock getLock() {
		return rwLock;
	}

}
