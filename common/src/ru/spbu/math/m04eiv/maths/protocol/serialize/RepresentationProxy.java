package ru.spbu.math.m04eiv.maths.protocol.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.code.annatasha.annotations.ThreadMarker;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

public interface RepresentationProxy<T> {
	
	@ThreadMarker
	interface Reader {}
	
	@ThreadMarker
	interface Writer {}
	
	@ExecPermissions(Writer.class)
	void writeToStream(T object, OutputStream stream) throws IOException;
	
	@ExecPermissions(Reader.class)
	T readFromStream(InputStream stream) throws IOException;

}
