package ru.spbu.math.m04eiv.maths.protocol.serialize.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.spbu.math.m04eiv.maths.protocol.commands.MultiplyMatrices;
import ru.spbu.math.m04eiv.maths.protocol.serialize.RepresentationProxy;

final class MultiplyMatricesRepresentation implements
		RepresentationProxy<MultiplyMatrices> {

	@Override
	public MultiplyMatrices readFromStream(InputStream stream)
			throws IOException {
		assert stream != null;
		
		DataInputStream dis = new DataInputStream(stream);
		
		final String lhs = dis.readUTF();
		final String rhs = dis.readUTF();
		final String dest = dis.readUTF();
		
		return new MultiplyMatrices(lhs, rhs, dest);
	}

	@Override
	public void writeToStream(MultiplyMatrices request, OutputStream stream)
			throws IOException {
		assert request != null;
		assert stream != null;
		
		final String[] names = request.getNames();
		DataOutputStream dos = new DataOutputStream(stream);
		
		dos.writeUTF(names[0]);
		dos.writeUTF(names[1]);
		dos.writeUTF(names[2]);
	}

}
