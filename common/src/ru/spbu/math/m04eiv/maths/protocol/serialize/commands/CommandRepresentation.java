package ru.spbu.math.m04eiv.maths.protocol.serialize.commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

import ru.spbu.math.m04eiv.maths.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.protocol.commands.CommandsVisitor;
import ru.spbu.math.m04eiv.maths.protocol.commands.GetMatrix;
import ru.spbu.math.m04eiv.maths.protocol.commands.MatrixResponse;
import ru.spbu.math.m04eiv.maths.protocol.commands.MultiplyMatrices;
import ru.spbu.math.m04eiv.maths.protocol.commands.SetMatrix;
import ru.spbu.math.m04eiv.maths.protocol.serialize.RepresentationProxy;

public final class CommandRepresentation implements
		RepresentationProxy<Command> {

	private final static CommandRepresentation INSTANCE = new CommandRepresentation();

	private enum Opcode {
		GET_MATRIX(new GetMatrixRepresentation()), SET_MATRIX(
				new SetMatrixRepresentation()), MULTIPLY_MATRICES(
				new MultiplyMatricesRepresentation()), MATRIX_REPONSE(
				new MatrixResponseRepresentation());

		private final RepresentationProxy<? extends Command> proxy;

		Opcode(RepresentationProxy<? extends Command> proxy) {
			this.proxy = proxy;
		}

		public RepresentationProxy<? extends Command> getProxy() {
			return proxy;
		}
	}

	private CommandRepresentation() {
	}

	@Override
	@ExecPermissions(Command.Constructor.class)
	public Command readFromStream(InputStream stream) throws IOException {
		assert stream != null;

		DataInputStream dis = new DataInputStream(stream);

		int operation = dis.readInt();

		if (operation < 0 || operation >= Opcode.values().length) {
			throw new IOException("Invalid OPCODE");
		}

		final Opcode code = Opcode.values()[operation];
		return code.getProxy().readFromStream(stream);
	}

	@Override
	@ExecPermissions({ Command.Reader.class, Command.Visitor.class })
	public void writeToStream(Command object, OutputStream stream)
			throws IOException {
		Writer w = new Writer(stream);
		object.acceptVisitor(w);
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
		@SuppressWarnings("unchecked")
		public void visit(GetMatrix command) {
			try {
				Opcode op = Opcode.GET_MATRIX;
				writeOpcode(op);
				((RepresentationProxy<GetMatrix>) op.getProxy()).writeToStream(
						command, stream);
			} catch (IOException e) {
				this.exception = e;
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public void visit(SetMatrix command) {
			try {
				Opcode op = Opcode.SET_MATRIX;
				writeOpcode(op);
				((RepresentationProxy<SetMatrix>) op.getProxy()).writeToStream(
						command, stream);
			} catch (IOException e) {
				this.exception = e;
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public void visit(MultiplyMatrices command) {
			try {
				Opcode op = Opcode.MULTIPLY_MATRICES;
				writeOpcode(op);
				((RepresentationProxy<MultiplyMatrices>) op.getProxy())
						.writeToStream(command, stream);
			} catch (IOException e) {
				this.exception = e;
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public void visit(MatrixResponse matrixResponse) {
			try {
				Opcode op = Opcode.MATRIX_REPONSE;
				writeOpcode(op);
				((RepresentationProxy<MatrixResponse>) op.getProxy())
						.writeToStream(matrixResponse, stream);
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
