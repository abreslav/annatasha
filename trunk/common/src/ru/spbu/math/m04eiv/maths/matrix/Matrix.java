package ru.spbu.math.m04eiv.maths.matrix;

import com.google.code.annatasha.annotations.Field;
import com.google.code.annatasha.annotations.Method;


public final class Matrix {

	public final static class Dimensions {
		public final int M;
		public final int N;

		public Dimensions(int M, int N) {
			this.M = M;
			this.N = N;
		}
	}

	
	@Field.WritePermissions(IMatrixWriter.class)
	@Field.ReadPermissions(IMatrixReader.class)
	private int[] data = null;
	
	@Field.WritePermissions(IMatrixWriter.class)
	@Field.ReadPermissions(IMatrixReader.class)
	private int M;
	
	@Field.WritePermissions(IMatrixWriter.class)
	@Field.ReadPermissions(IMatrixReader.class)
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

	@Method.ExecPermissions(IMatrixWriter.class)
	public void setSize(int M, int N) {
		this.M = M;
		this.N = N;

		if (data.length < M * N) {
			data = new int[M * N];
		}
	}

	@Method.ExecPermissions(IMatrixReader.class)
	public Dimensions getSize() {
		return new Dimensions(M, N);
	}

	@Method.ExecPermissions(IMatrixWriter.class)
	public void setCell(int m, int n, int val) {
		data[m * N + n] = val;
	}

	@Method.ExecPermissions(IMatrixReader.class)
	public int getCell(int m, int n) {
		return data[m * N + n];
	}

	@Method.ExecPermissions(IMatrixWriter.class)
	public void copyFrom(Matrix matrix) {
		Dimensions d = matrix.getSize();
		setSize(d.M, d.N);
		System.arraycopy(matrix.data, 0, data, 0, matrix.data.length);
	}

}
