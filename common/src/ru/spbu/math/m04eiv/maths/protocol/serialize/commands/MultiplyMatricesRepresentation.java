package ru.spbu.math.m04eiv.maths.protocol.serialize.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.spbu.math.m04eiv.maths.protocol.commands.MultiplyMatrices;
import ru.spbu.math.m04eiv.maths.protocol.serialize.RepresentationProxy;
import ru.spbu.math.m04eiv.maths.protocol.serialize.StringRepresentation;

final class MultiplyMatricesRepresentation implements
		RepresentationProxy<MultiplyMatrices> {

	@Override
	public MultiplyMatrices readFromStream(InputStream stream)
			throws IOException {
		
		final String lhs = StringRepresentation.getStringFromStream(stream);
		final String rhs = StringRepresentation.getStringFromStream(stream);
		final String dest = StringRepresentation.getStringFromStream(stream);
		
		return new MultiplyMatrices(lhs, rhs, dest);
	}

	@Override
	public void writeToStream(MultiplyMatrices object, OutputStream stream)
			throws IOException {
		final String[] names = object.getNames();
		StringRepresentation.putStringToStream(names[0], stream);
		StringRepresentation.putStringToStream(names[1], stream);
		StringRepresentation.putStringToStream(names[2], stream);
	}

}
