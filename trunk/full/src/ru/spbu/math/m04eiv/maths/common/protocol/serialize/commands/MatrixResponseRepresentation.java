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

package ru.spbu.math.m04eiv.maths.common.protocol.serialize.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

import ru.spbu.math.m04eiv.maths.common.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.common.protocol.Status;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.MatrixResponse;
import ru.spbu.math.m04eiv.maths.common.protocol.serialize.MatrixRepresentation;
import ru.spbu.math.m04eiv.maths.common.protocol.serialize.RepresentationProxy;
import ru.spbu.math.m04eiv.maths.common.protocol.serialize.TBinaryStreamReader;

public final class MatrixResponseRepresentation implements RepresentationProxy {

	@Override
	@ExecPermissions(TBinaryStreamReader.class)
	public MatrixResponse readFromStream(InputStream stream) throws IOException {
		assert stream != null;

		DataInputStream dis = new DataInputStream(stream);

		final int uid = dis.readInt();
		final int statusOrd = dis.readInt();

		if (statusOrd < 0 || statusOrd >= Status.values().length) {
			throw new IOException("Invalid status value");
		}
		final Status status = Status.values()[statusOrd];
		Matrix matrix = null;
		if (status == Status.Ready) {
			matrix = MatrixRepresentation.getMatrixFromStream(dis);
		}

		return new MatrixResponse(uid, status, matrix);
	}

	@Override
	public void writeToStream(Object object, OutputStream stream)
			throws IOException {
		MatrixResponse response = (MatrixResponse) object;
		assert response != null;
		assert stream != null;

		DataOutputStream dos = new DataOutputStream(stream);

		dos.writeInt(response.getUid());
		dos.writeInt(response.getStatus().ordinal());
		if (response.getStatus() == Status.Ready) {
			MatrixRepresentation.putMatrixToStream(response.getMatrix(), dos);
		}
	}

}
