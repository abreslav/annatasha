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

package ru.spbu.math.m04eiv.maths.common.matrix;

public final class Matrix {

	public final static class Dimensions {
		public final int M;
		public final int N;

		public Dimensions(int M, int N) {
			this.M = M;
			this.N = N;
		}
	}

	private int[] data = null;

	private int M;

	private int N;

	public Matrix(int M, int N) {
		this.M = M;
		this.N = N;
		this.data = new int[M * N];
	}

	public Matrix(int M, int N, int[] data) {
		this(M, N);
		System.arraycopy(data, 0, this.data, 0, data.length);
	}

	public void setSize(int M, int N) {
		this.M = M;
		this.N = N;

		if (data.length < M * N) {
			data = new int[M * N];
		}
	}

	public Dimensions getSize() {
		return new Dimensions(M, N);
	}

	public void setCell(int m, int n, int val) {
		data[m * N + n] = val;
	}

	public int getCell(int m, int n) {
		return data[m * N + n];
	}

	public void copyFrom(Matrix matrix) {
		Dimensions d = matrix.getSize();
		setSize(d.M, d.N);
		System.arraycopy(matrix.data, 0, data, 0, matrix.data.length);
	}

}
