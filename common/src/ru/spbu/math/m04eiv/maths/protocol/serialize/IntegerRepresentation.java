package ru.spbu.math.m04eiv.maths.protocol.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IntegerRepresentation implements
		RepresentationProxy<Integer> {

	private final static IntegerRepresentation INSTANCE = new IntegerRepresentation();

	private IntegerRepresentation() {
	}

	public static int getIntFromStream(InputStream stream) throws IOException {
		return INSTANCE.readIntFromStream(stream);
	}

	public static void putIntToStream(int value, OutputStream stream)
			throws IOException {
		INSTANCE.writeIntToStream(value, stream);
	}

	public int readIntFromStream(InputStream stream) throws IOException {
		assert stream != null;

		final byte[] buf = new byte[4];
		Serializer.read(stream, buf);
		return IntegerRepresentation.byteArrayToInt(buf, 0);
	}

	public void writeIntToStream(int value, OutputStream stream)
			throws IOException {
		assert stream != null;

		final byte[] buf = new byte[4];
		IntegerRepresentation.intToByteArray(value, buf, 0);
		Serializer.write(stream, buf);
	}

	@Override
	public Integer readFromStream(InputStream stream) throws IOException {
		assert stream != null;
		return readIntFromStream(stream);
	}

	@Override
	public void writeToStream(Integer object, OutputStream stream)
			throws IOException {
		assert object != null;
		assert stream != null;

		writeIntToStream(object, stream);
	}

	public static int byteArrayToInt(byte[] array, int offset) {
		assert array != null;
		assert offset + 4 <= array.length;
		
		return array[offset] 
		             | (array[offset + 1] << 8)
				| (array[offset + 2] << 16)
				| (array[offset + 3] << 24);
	}

	public static void intToByteArray(int src, byte[] dest, int offs) {
		assert dest != null;
		assert offs + 4 <= dest.length;
		
		dest[offs] = (byte) src;
		dest[offs + 1] = (byte) (src >>> 8);
		dest[offs + 2] = (byte) (src >>> 16);
		dest[offs + 3] = (byte) (src >>> 24);
	}

}
