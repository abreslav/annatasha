/*******************************************************************************
 * Copyright (c) 2008, 2009 Ivan Egorov <egorich.3.04@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ivan Egorov <egorich.3.04@gmail.com>
 *******************************************************************************/

package ru.spbu.math.m04eiv.maths.common.protocol.serialize.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.spbu.math.m04eiv.maths.common.protocol.commands.MultiplyMatrices;
import ru.spbu.math.m04eiv.maths.common.protocol.serialize.RepresentationProxy;

final class MultiplyMatricesRepresentation implements
		RepresentationProxy {

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
	public void writeToStream(Object object, OutputStream stream)
			throws IOException {
		MultiplyMatrices request = (MultiplyMatrices) object;
		assert request != null;
		assert stream != null;
		
		final String[] names = request.getNames();
		DataOutputStream dos = new DataOutputStream(stream);
		
		dos.writeUTF(names[0]);
		dos.writeUTF(names[1]);
		dos.writeUTF(names[2]);
	}

}
