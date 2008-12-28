package ru.spbu.math.m04eiv.maths.protocol.serialize.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.spbu.math.m04eiv.maths.protocol.commands.GetMatrix;
import ru.spbu.math.m04eiv.maths.protocol.serialize.IntegerRepresentation;
import ru.spbu.math.m04eiv.maths.protocol.serialize.RepresentationProxy;
import ru.spbu.math.m04eiv.maths.protocol.serialize.StringRepresentation;

final class GetMatrixRepresentation implements RepresentationProxy<GetMatrix> {

	@Override
	public GetMatrix readFromStream(InputStream stream) throws IOException {
		int uid = IntegerRepresentation.getIntFromStream(stream);
		String name = StringRepresentation.getStringFromStream(stream);
		
		return new GetMatrix(uid, name);
	}

	@Override
	public void writeToStream(GetMatrix object, OutputStream stream)
			throws IOException {
		IntegerRepresentation.putIntToStream(object.getUid(), stream);
		StringRepresentation.putStringToStream(object.getName(), stream);
	}

}
