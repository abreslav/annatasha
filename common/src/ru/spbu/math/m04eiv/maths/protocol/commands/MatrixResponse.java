package ru.spbu.math.m04eiv.maths.protocol.commands;

import ru.spbu.math.m04eiv.maths.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.protocol.Status;

public final class MatrixResponse extends Command {

	private final int uid;
	private final Status status;
	private final Matrix matrix;

	/**
	 * 
	 * @param uid
	 * @param status
	 * @param matrix
	 *            Matrix to write down to response. It's not copied!
	 */
	public MatrixResponse(int uid, Status status, Matrix matrix) {
		this.uid = uid;
		this.status = status;
		this.matrix = matrix;
	}

	public int getUid() {
		return uid;
	}

	public Status getStatus() {
		return status;
	}

	public Matrix getMatrix() {
		return matrix;
	}

	@Override
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
