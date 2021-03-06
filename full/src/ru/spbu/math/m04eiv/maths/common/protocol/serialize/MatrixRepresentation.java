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

package ru.spbu.math.m04eiv.maths.common.protocol.serialize;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.spbu.math.m04eiv.maths.common.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.common.matrix.Matrix.Dimensions;

public class MatrixRepresentation implements RepresentationProxy {

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
	public void writeToStream(Object object, OutputStream stream)
			throws IOException {
		Matrix matrix = (Matrix) object;
		
		assert matrix != null;
		assert stream != null;
		
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream( stream ));

		final Dimensions dim = matrix.getSize();

		dos.writeInt(dim.M);
		dos.writeInt(dim.N);

		for (int m = 0; m < dim.M; ++m) {
			for (int n = 0; n < dim.N; ++n) {
				dos.writeInt(matrix.getCell(m, n));
			}
		}
		dos.flush();
	}

}
