package ru.spbu.math.m04eiv.maths.protocol.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class StringRepresentation implements RepresentationProxy<String> {

	private final static StringRepresentation INSTANCE = new StringRepresentation();

	private StringRepresentation() {
	}

	public final static String getStringFromStream(InputStream stream)
			throws IOException {
		assert stream != null;
		return INSTANCE.readFromStream(stream);
	}

	public final static void putStringToStream(String string,
			OutputStream stream) throws IOException {
		assert stream != null;
		
		INSTANCE.writeToStream(string, stream);
	}

	@Override
	public String readFromStream(InputStream stream) throws IOException {
		assert stream != null;

		final int len = IntegerRepresentation.getIntFromStream(stream);
		if (len < 0) {
			throw new IOException("String's length must be non-negative");
		}

		final byte[] stringBuffer = new byte[len];
		Serializer.read(stream, stringBuffer);

		return new String(stringBuffer, "UTF-8");
	}

	@Override
	public void writeToStream(String object, OutputStream stream)
			throws IOException {
		final byte[] stringBuffer = object.getBytes("UTF-8");
		IntegerRepresentation.putIntToStream(stringBuffer.length, stream);
		Serializer.write(stream, stringBuffer);
	}

}
