package ru.spbu.math.m04eiv.maths.protocol.serialize.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.spbu.math.m04eiv.maths.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.protocol.commands.SetMatrix;
import ru.spbu.math.m04eiv.maths.protocol.serialize.MatrixRepresentation;
import ru.spbu.math.m04eiv.maths.protocol.serialize.RepresentationProxy;

final class SetMatrixRepresentation implements RepresentationProxy<SetMatrix> {

	@Override
	public SetMatrix readFromStream(InputStream stream) throws IOException {
		assert stream != null;
		
		DataInputStream dis = new DataInputStream(stream);
		
		final String name = dis.readUTF();
		final Matrix matrix = MatrixRepresentation.getMatrixFromStream(dis);
		return new SetMatrix(name, matrix);
	}

	@Override
	public void writeToStream(SetMatrix request, OutputStream stream)
			throws IOException {
		assert request != null;
		assert stream != null;
		
		DataOutputStream dos = new DataOutputStream(stream);
		
		dos.writeUTF(request.getName());
		MatrixRepresentation.putMatrixToStream(request.getMatrix(), dos);
	}

}