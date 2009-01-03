package ru.spbu.math.m04eiv.maths.protocol.serialize;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.spbu.math.m04eiv.maths.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.matrix.Matrix.Dimensions;

public class MatrixRepresentation implements RepresentationProxy<Matrix> {

	private final static MatrixRepresentation INSTANCE = new MatrixRepresentation();

	private MatrixRepresentation() {
	}

	public static Matrix getMatrixFromStream(InputStream stream)
			throws IOException {
		assert stream != null;

		return INSTANCE.readFromStream(stream);
	}

	public static void putMatrixToStream(Matrix matrix, OutputStream stream)
			throws IOException {
		assert stream != null;

		INSTANCE.writeToStream(matrix, stream);
	}

	@Override
	public Matrix readFromStream(InputStream stream) throws IOException {
		assert stream != null;
		
		DataInputStream dis = new DataInputStream(stream);

		// I don't use IntegerRepresentation to avoid multiple allocations of
		// small array chunks during IntegerRepresentation.putIntToStream()
		final int M = dis.readInt();
		final int N = dis.readInt();

		if (M <= 0 || N <= 0) {
			throw new IOException("Invalid matrix size: " + M + ", " + N);
		}

		final Matrix matrix = new Matrix(M, N);
		for (int m = 0; m < M; ++m) {
			for (int n = 0; n < N; ++n) {
				matrix.setCell(m, n, dis.readInt());
			}
		}

		return matrix;
	}

	@Override
	public void writeToStream(Matrix matrix, OutputStream stream)
			throws IOException {
		assert matrix != null;
		assert stream != null;
		
		DataOutputStream dos = new DataOutputStream(stream);

		final Dimensions dim = matrix.getSize();

		dos.writeInt(dim.M);
		dos.writeInt(dim.N);

		for (int m = 0; m < dim.M; ++m) {
			for (int n = 0; n < dim.N; ++n) {
				dos.writeInt(matrix.getCell(m, n));
			}
		}
	}

}
