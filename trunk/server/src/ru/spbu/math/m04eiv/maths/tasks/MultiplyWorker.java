/*******************************************************************************
 * Copyright (c) 2008, 2009 Ivan Egorov <egorich.3.04@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ivan Egorov <egorich.3.04@gmail.com>
 *******************************************************************************/

/**
 * 
 */
package ru.spbu.math.m04eiv.maths.tasks;

import ru.spbu.math.m04eiv.maths.matrix.TMatrixWriter;
import ru.spbu.math.m04eiv.maths.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.processor.Worker;

final class MultiplyWorker implements Worker, TMatrixWriter {
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