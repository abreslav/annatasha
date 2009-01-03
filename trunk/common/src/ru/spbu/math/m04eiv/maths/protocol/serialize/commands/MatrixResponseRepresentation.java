package ru.spbu.math.m04eiv.maths.protocol.serialize.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.spbu.math.m04eiv.maths.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.protocol.Status;
import ru.spbu.math.m04eiv.maths.protocol.commands.MatrixResponse;
import ru.spbu.math.m04eiv.maths.protocol.serialize.MatrixRepresentation;
import ru.spbu.math.m04eiv.maths.protocol.serialize.RepresentationProxy;

public final class MatrixResponseRepresentation implements
		RepresentationProxy<MatrixResponse> {

	@Override
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
	public void writeToStream(MatrixResponse response, OutputStream stream)
			throws IOException {
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
