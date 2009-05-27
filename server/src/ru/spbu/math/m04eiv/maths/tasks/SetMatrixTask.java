package ru.spbu.math.m04eiv.maths.tasks;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

import ru.spbu.math.m04eiv.maths.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.matrix.MatrixDescriptor;
import ru.spbu.math.m04eiv.maths.matrix.MatrixPool;
import ru.spbu.math.m04eiv.maths.processor.Task;
import ru.spbu.math.m04eiv.maths.processor.WorkersManager;
import ru.spbu.math.m04eiv.maths.protocol.Status;
import ru.spbu.math.m04eiv.maths.protocol.commands.SetMatrix;

final class SetMatrixTask extends Task {
	
	private final Matrix src;
	private final MatrixPool pool;
	
	private final String[] writeDescriptors;
	private final static String[] readDescriptors = new String[0];
	
	private MatrixPool.Lock lock;

	/**
	 * 
	 * @param man
	 * @param pool
	 */
	public SetMatrixTask(WorkersManager man, MatrixPool pool, SetMatrix command) {
		super(man, "SET " + command.getName());
		
		this.pool = pool;
		this.src = command.getMatrix();

		writeDescriptors = new String[] { command.getName() };
	}

	@Override
	@ExecPermissions(Task.TExecutor.class)
	public void execute() {
		final MatrixDescriptor dest = lock.getWriteDescriptor(0);
		dest.getMatrix().copyFrom(src);
		dest.setStatus(Status.Ready);
		
		done();
	}

	@Override
	public void interrupt() {}

	@Override
	public void releaseResources() {
		pool.releaseLock(lock);
	}

	@Override
	public boolean tryFetchResources() {
		lock = pool.tryAcquireLock(readDescriptors, writeDescriptors);
		return lock.isAcquired();
	}

}
