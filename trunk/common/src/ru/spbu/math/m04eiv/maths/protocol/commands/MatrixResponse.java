package ru.spbu.math.m04eiv.maths.protocol.commands;

import com.google.code.annatasha.annotations.Field.ReadPermissions;
import com.google.code.annatasha.annotations.Field.WritePermissions;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

import ru.spbu.math.m04eiv.maths.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.protocol.Status;

public final class MatrixResponse extends Command {

	@ReadPermissions(Command.Reader.class)
	@WritePermissions(Command.Constructor.class)
	private final int uid;

	@ReadPermissions(Command.Reader.class)
	@WritePermissions(Command.Constructor.class)
	private final Status status;

	@ReadPermissions(Command.Reader.class)
	@WritePermissions(Command.Constructor.class)
	private final Matrix matrix;

	/**
	 * 
	 * @param uid
	 * @param status
	 * @param matrix
	 *            Matrix to write down to response. It's not copied!
	 */
	@ExecPermissions(Command.Constructor.class)
	public MatrixResponse(int uid, Status status, Matrix matrix) {
		this.uid = uid;
		this.status = status;
		this.matrix = matrix;
	}

	@ExecPermissions(Command.Reader.class)
	public int getUid() {
		return uid;
	}

	@ExecPermissions(Command.Reader.class)
	public Status getStatus() {
		return status;
	}

	@ExecPermissions(Command.Reader.class)
	public Matrix getMatrix() {
		return matrix;
	}

	@Override
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
