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

import ru.spbu.math.m04eiv.maths.common.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.CommandsVisitor;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.GetMatrix;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.MatrixResponse;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.MultiplyMatrices;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.SetMatrix;
import ru.spbu.math.m04eiv.maths.common.protocol.serialize.RepresentationProxy;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public final class CommandRepresentation implements RepresentationProxy {

	private final static CommandRepresentation INSTANCE = new CommandRepresentation();

	private enum Opcode {
		GET_MATRIX(new GetMatrixRepresentation()), SET_MATRIX(
				new SetMatrixRepresentation()), MULTIPLY_MATRICES(
				new MultiplyMatricesRepresentation()), MATRIX_REPONSE(
				new MatrixResponseRepresentation());

		private final RepresentationProxy proxy;

		Opcode(RepresentationProxy proxy) {
			this.proxy = proxy;
		}

		public RepresentationProxy getProxy() {
			return proxy;
		}
	}

	private CommandRepresentation() {
	}

	@Override
	@ExecPermissions(RepresentationProxy.StreamReader.class)
	public Command readFromStream(InputStream stream) throws IOException {
		assert stream != null;

		DataInputStream dis = new DataInputStream(stream);

		int operation = dis.readInt();

		if (operation < 0 || operation >= Opcode.values().length) {
			throw new IOException("Invalid OPCODE");
		}

		final Opcode code = Opcode.values()[operation];
		return (Command) code.getProxy().readFromStream(stream);
	}

	@Override
	public void writeToStream(Object object, OutputStream stream)
			throws IOException {
		Command command = (Command) object;
		Writer w = new Writer(stream);
		command.acceptVisitor(w);
		IOException exception = w.getException();
		if (exception != null) {
			throw exception;
		}
	}

	public static CommandRepresentation getInstance() {
		return INSTANCE;
	}

	private final static class Writer implements CommandsVisitor {
		private final OutputStream stream;
		private IOException exception;

		private Writer(OutputStream stream) {
			this.stream = stream;
			this.exception = null;
		}

		@Override
		public void visit(GetMatrix command) {
			try {
				Opcode op = Opcode.GET_MATRIX;
				writeOpcode(op);
				op.getProxy().writeToStream(command, stream);
			} catch (IOException e) {
				this.exception = e;
			}
		}

		@Override
		public void visit(SetMatrix command) {
			try {
				Opcode op = Opcode.SET_MATRIX;
				writeOpcode(op);
				op.getProxy().writeToStream(command, stream);
			} catch (IOException e) {
				this.exception = e;
			}
		}

		@Override
		public void visit(MultiplyMatrices command) {
			try {
				Opcode op = Opcode.MULTIPLY_MATRICES;
				writeOpcode(op);
				op.getProxy().writeToStream(command, stream);
			} catch (IOException e) {
				this.exception = e;
			}
		}

		@Override
		public void visit(MatrixResponse matrixResponse) {
			try {
				Opcode op = Opcode.MATRIX_REPONSE;
				writeOpcode(op);
				op.getProxy().writeToStream(matrixResponse, stream);
			} catch (IOException e) {
				this.exception = e;
			}
		}

		public IOException getException() {
			return exception;
		}

		/**
		 * @param op
		 * @throws IOException
		 */
		private void writeOpcode(Opcode op) throws IOException {
			DataOutputStream dos = new DataOutputStream(stream);
			dos.writeInt(op.ordinal());
		}

	}

}
