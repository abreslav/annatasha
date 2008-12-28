package ru.spbu.math.m04eiv.maths.tasks.server;

import java.util.concurrent.atomic.AtomicInteger;

import ru.spbu.math.m04eiv.maths.matrix.MatrixDescriptor;
import ru.spbu.math.m04eiv.maths.matrix.MatrixPool;
import ru.spbu.math.m04eiv.maths.matrix.Matrix.Dimensions;
import ru.spbu.math.m04eiv.maths.matrix.MatrixPool.Lock;
import ru.spbu.math.m04eiv.maths.processor.Task;
import ru.spbu.math.m04eiv.maths.processor.WorkersManager;
import ru.spbu.math.m04eiv.maths.protocol.Status;
import ru.spbu.math.m04eiv.maths.protocol.commands.MultiplyMatrices;

final class MuliplyMatricesTask extends Task {

	public final String lhsName;
	public final String rhsName;
	public final String destName;

	private final MatrixPool pool;

	private final AtomicInteger workers;

	private volatile int taskSize;
	private Lock lock;

	public MuliplyMatricesTask(WorkersManager man, MatrixPool pool, MultiplyMatrices command) {
		super(man, command.getNames()[2] + " = " + command.getNames()[0] + " x " + command.getNames()[1]);
		final String[] names = command.getNames();

		this.pool = pool;

		this.lhsName = names[0];
		this.rhsName = names[1];
		this.destName = names[2];

		this.workers = new AtomicInteger(0);

		this.taskSize = 0;
	}

	@Override
	public void execute() {
		final MatrixDescriptor lhs = lock.getReadDescriptor(0);
		final MatrixDescriptor rhs = lock.getReadDescriptor(1);
		final MatrixDescriptor dest = lock.getWriteDescriptor(0);

		dest.setStatus(Status.Processing);
		if (lhs.getStatus() != Status.Ready || rhs.getStatus() != Status.Ready) {
			dest.setStatus(Status.Error);
			done();
			return;
		}

		final Dimensions l = lhs.getMatrix().getSize();
		final Dimensions r = rhs.getMatrix().getSize();

		if (l.N != r.M) {
			dest.setStatus(Status.Error);
			done();
			return;
		}

		taskSize = l.M * r.N;
		workers.set(taskSize);

		getListener().taskStarted(this, taskSize);

		final int mid = l.N;
		for (int m = 0; m < l.M; ++m) {
			for (int n = 0; n < r.N; ++n) {
				final int M = m;
				final int N = n;
				enqueueWorker(new MultiplyWorker(this, lhs.getMatrix(), rhs
						.getMatrix(), dest.getMatrix(), mid, M, N));
			}
		}

	}

	@Override
	public void releaseResources() {
		pool.releaseLock(lock);
	}

	@Override
	public boolean tryFetchResources() {
		lock = pool.tryAcquireLock(new String[] { lhsName, rhsName },
				new String[] { destName });
		return lock.isAcquired();
	}

	@Override
	public void interrupt() {
	}

	public void decreaseWorkersCount() {
		int left = workers.decrementAndGet();
		getListener().taskProgress(this, taskSize - left);

		if (left == 0) {
			done();
		}
	}

}
