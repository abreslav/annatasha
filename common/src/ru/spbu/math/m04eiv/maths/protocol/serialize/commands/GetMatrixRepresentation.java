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

package ru.spbu.math.m04eiv.maths.protocol.serialize.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.spbu.math.m04eiv.maths.protocol.commands.GetMatrix;
import ru.spbu.math.m04eiv.maths.protocol.serialize.RepresentationProxy;

final class GetMatrixRepresentation implements RepresentationProxy<GetMatrix> {

	@Override
	public GetMatrix readFromStream(InputStream stream) throws IOException {
		DataInputStream dis = new DataInputStream(stream);
		int uid = dis.readInt();
		String name = dis.readUTF();
		
		return new GetMatrix(uid, name);
	}

	@Override
	public void writeToStream(GetMatrix matrix, OutputStream stream)
			throws IOException {
		DataOutputStream dos = new DataOutputStream(stream);
		dos.writeInt(matrix.getUid());
		dos.writeUTF(matrix.getName());
	}

}
