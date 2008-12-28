package ru.spbu.math.m04eiv.maths.protocol.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface RepresentationProxy<T> {
	
	void writeToStream(T object, OutputStream stream) throws IOException;
	T readFromStream(InputStream stream) throws IOException;

}
