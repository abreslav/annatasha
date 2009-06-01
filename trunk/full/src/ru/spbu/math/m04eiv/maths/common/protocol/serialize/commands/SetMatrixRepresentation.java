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

import ru.spbu.math.m04eiv.maths.common.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.SetMatrix;
import ru.spbu.math.m04eiv.maths.common.protocol.serialize.MatrixRepresentation;
import ru.spbu.math.m04eiv.maths.common.protocol.serialize.RepresentationProxy;

final class SetMatrixRepresentation implements RepresentationProxy {

	@Override
	public SetMatrix readFromStream(InputStream stream) throws IOException {
		assert stream != null;
		
		DataInputStream dis = new DataInputStream(stream);
		
		final String name = dis.readUTF();
		final Matrix matrix = MatrixRepresentation.getMatrixFromStream(dis);
		return new SetMatrix(name, matrix);
	}

	@Override
	public void writeToStream(Object object, OutputStream stream)
			throws IOException {
		SetMatrix request = (SetMatrix) object;
		assert request != null;
		assert stream != null;
		
		DataOutputStream dos = new DataOutputStream(stream);
		
		dos.writeUTF(request.getName());
		MatrixRepresentation.putMatrixToStream(request.getMatrix(), dos);
	}

}
