package ru.spbu.math.m04eiv.maths.protocol.serialize;

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

		// I don't use IntegerRepresentation to avoid multiple allocations of
		// small array chunks during IntegerRepresentation.putIntToStream()
		final byte[] buf = new byte[8];
		Serializer.read(stream, buf);
		final int M = IntegerRepresentation.byteArrayToInt(buf, 0);
		final int N = IntegerRepresentation.byteArrayToInt(buf, 4);

		if (M <= 0 || N <= 0) {
			throw new IOException("Invalid matrix size: " + M + ", " + N);
		}

		final Matrix matrix = new Matrix(M, N);
		for (int m = 0; m < M; ++m) {
			for (int n = 0; n < N; ++n) {
				Serializer.read(stream, buf, 0, 4);
				matrix.setCell(m, n, IntegerRepresentation.byteArrayToInt(buf,
						0));
			}
		}

		return matrix;
	}

	@Override
	public void writeToStream(Matrix matrix, OutputStream stream)
			throws IOException {
		assert matrix != null;
		assert stream != null;

		final Dimensions dim = matrix.getSize();

		// I don't use IntegerRepresentation to avoid multiple allocations of
		// small array chunks during IntegerRepresentation.putIntToStream()
		final byte[] outputBuffer = new byte[8];

		IntegerRepresentation.intToByteArray(dim.M, outputBuffer, 0);
		IntegerRepresentation.intToByteArray(dim.N, outputBuffer, 4);
		Serializer.write(stream, outputBuffer);

		for (int m = 0; m < dim.M; ++m) {
			for (int n = 0; n < dim.N; ++n) {
				IntegerRepresentation.intToByteArray(matrix.getCell(m, n),
						outputBuffer, 0);
				Serializer.write(stream, outputBuffer, 0, 4);
			}
		}
	}

}
