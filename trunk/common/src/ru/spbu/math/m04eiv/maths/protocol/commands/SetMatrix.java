package ru.spbu.math.m04eiv.maths.protocol.commands;

import com.google.code.annatasha.annotations.Field.ReadPermissions;
import com.google.code.annatasha.annotations.Field.WritePermissions;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

import ru.spbu.math.m04eiv.maths.matrix.Matrix;

public class SetMatrix extends Command {

	@ReadPermissions(Command.Reader.class)
	@WritePermissions(Command.Constructor.class)
	private final String name;
	
	@ReadPermissions(Command.Reader.class)
	@WritePermissions(Command.Constructor.class)
	private final Matrix matrix;

	/**
	 * 
	 * @param name
	 *            The name of matrix
	 * @param matrix
	 *            Matrix itself, it's not copied!
	 */
	@ExecPermissions(Command.Constructor.class)
	public SetMatrix(String name, Matrix matrix) {
		this.name = name;
		this.matrix = matrix;
	}

	@ExecPermissions(Command.Constructor.class)
	public SetMatrix(String name, int m, int n, int[] data) {
		this.name = name;
		matrix = new Matrix(m, n, data);
	}

	@ExecPermissions(Command.Reader.class)
	public String getName() {
		return name;
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
