package ru.spbu.math.m04eiv.maths.protocol.serialize.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.spbu.math.m04eiv.maths.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.protocol.Status;
import ru.spbu.math.m04eiv.maths.protocol.commands.MatrixResponse;
import ru.spbu.math.m04eiv.maths.protocol.serialize.IntegerRepresentation;
import ru.spbu.math.m04eiv.maths.protocol.serialize.MatrixRepresentation;
import ru.spbu.math.m04eiv.maths.protocol.serialize.RepresentationProxy;

public final class MatrixResponseRepresentation implements
		RepresentationProxy<MatrixResponse> {

	@Override
	public MatrixResponse readFromStream(InputStream stream) throws IOException {
		assert stream != null;
		
		final int uid = IntegerRepresentation.getIntFromStream(stream);
		final int statusOrd = IntegerRepresentation.getIntFromStream(stream);
		
		if (statusOrd < 0 || statusOrd >= Status.values().length) {
			throw new IOException("Invalid status value");
		}
		final Status status = Status.values()[statusOrd];
		Matrix matrix = null;
		if (status == Status.Ready) {
			matrix = MatrixRepresentation.getMatrixFromStream(stream);
		}
		
		return new MatrixResponse(uid, status, matrix);
	}

	@Override
	public void writeToStream(MatrixResponse object, OutputStream stream)
			throws IOException {
		assert object != null;
		assert stream != null;
		
		IntegerRepresentation.putIntToStream(object.getUid(), stream);
		IntegerRepresentation.putIntToStream(object.getStatus().ordinal(), stream);
		if (object.getStatus() == Status.Ready) {
			MatrixRepresentation.putMatrixToStream(object.getMatrix(), stream);
		}
	}

}
