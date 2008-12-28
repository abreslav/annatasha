package ru.spbu.math.m04eiv.maths.protocol.commands;

import ru.spbu.math.m04eiv.maths.matrix.Matrix;

public class SetMatrix extends Command {

	private final String name;
	private final Matrix matrix;

	/**
	 * 
	 * @param name
	 *            The name of matrix
	 * @param matrix
	 *            Matrix itself, it's not copied!
	 */
	public SetMatrix(String name, Matrix matrix) {
		this.name = name;
		this.matrix = matrix;
	}

	public SetMatrix(String name, int m, int n, int[] data) {
		this.name = name;
		matrix = new Matrix(m, n, data);
	}

	public String getName() {
		return name;
	}
	
	public Matrix getMatrix() {
		return matrix;
	}

	@Override
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
