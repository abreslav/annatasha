package ru.spbu.math.m04eiv.maths.protocol.commands;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

@ExecPermissions(Command.TReader.class)
public interface CommandsVisitor {

	public void visit(GetMatrix command);
	public void visit(SetMatrix command);
	public void visit(MultiplyMatrices command);
	public void visit(MatrixResponse matrixResponse);

}
