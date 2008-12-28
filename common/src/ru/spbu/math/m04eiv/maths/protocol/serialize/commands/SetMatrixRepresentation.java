package ru.spbu.math.m04eiv.maths.protocol.serialize.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.spbu.math.m04eiv.maths.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.protocol.commands.SetMatrix;
import ru.spbu.math.m04eiv.maths.protocol.serialize.MatrixRepresentation;
import ru.spbu.math.m04eiv.maths.protocol.serialize.RepresentationProxy;
import ru.spbu.math.m04eiv.maths.protocol.serialize.StringRepresentation;

final class SetMatrixRepresentation implements RepresentationProxy<SetMatrix> {

	@Override
	public SetMatrix readFromStream(InputStream stream) throws IOException {
		assert stream != null;
		
		final String name = StringRepresentation.getStringFromStream(stream);
		final Matrix matrix = MatrixRepresentation.getMatrixFromStream(stream);
		return new SetMatrix(name, matrix);
	}

	@Override
	public void writeToStream(SetMatrix object, OutputStream stream)
			throws IOException {
		assert object != null;
		assert stream != null;
		
		StringRepresentation.putStringToStream(object.getName(), stream);
		MatrixRepresentation.putMatrixToStream(object.getMatrix(), stream);
	}

}
