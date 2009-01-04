/**
 * 
 */
package ru.spbu.math.m04eiv.maths.tasks;

import ru.spbu.math.m04eiv.maths.matrix.IMatrixReader;
import ru.spbu.math.m04eiv.maths.matrix.IMatrixWriter;
import ru.spbu.math.m04eiv.maths.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.processor.Worker;

final class MultiplyWorker implements Worker, IMatrixReader, IMatrixWriter {
	/**
	 * 
	 */
	private final MuliplyMatricesTask muliplyTask;
	private final int mid;
	private final int m;
	private final int n;
	
	private final Matrix lhs;
	private final Matrix rhs;
	private final Matrix dest;

	MultiplyWorker(MuliplyMatricesTask muliplyMatricesTask, Matrix lhs,
			Matrix rhs, Matrix dest, int mid, int m, int n) {
		muliplyTask = muliplyMatricesTask;
		this.lhs = lhs;
		this.rhs = rhs;
		this.dest = dest;

		this.mid = mid;
		this.m = m;
		this.n = n;
	}

	@Override
	public void run() {

		if (!muliplyTask.isInterrupted()) {
			int acc = 0;
			for (int i = 0; i < mid; ++i) {
				acc += lhs.getCell(m, i) * rhs.getCell(i, n);
			}
			dest.setCell(m, n, acc);
			muliplyTask.decreaseWorkersCount();
		}
	}
}