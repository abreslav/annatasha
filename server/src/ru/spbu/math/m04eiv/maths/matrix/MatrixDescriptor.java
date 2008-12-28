package ru.spbu.math.m04eiv.maths.matrix;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.spbu.math.m04eiv.maths.protocol.Status;

public final class MatrixDescriptor {
	
	private volatile Status status = Status.Processing;

	private final Matrix matrix = new Matrix(0, 0);
	
	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
	
	public Matrix getMatrix() {
		return matrix;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}
	
	public ReadWriteLock getLock() {
		return rwLock;
	}

}
