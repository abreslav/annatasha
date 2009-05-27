package ru.spbu.math.m04eiv.maths.protocol.commands;

import com.google.code.annatasha.annotations.Field.ReadPermissions;
import com.google.code.annatasha.annotations.Field.WritePermissions;
import com.google.code.annatasha.annotations.Method.ExecPermissions;


public final class MultiplyMatrices extends Command {

	@ReadPermissions(Command.TReader.class)
	@WritePermissions(Command.TConstructor.class)
	private final String lhsName;
	
	@ReadPermissions(Command.TReader.class)
	@WritePermissions(Command.TConstructor.class)
	private final String rhsName;

	@ReadPermissions(Command.TReader.class)
	@WritePermissions(Command.TConstructor.class)
	private final String destName;

	@ExecPermissions(Command.TConstructor.class)
	public MultiplyMatrices(String lhsName, String rhsName, String destName) {
		this.lhsName = lhsName;
		this.rhsName = rhsName;
		this.destName = destName;
	}

	@ExecPermissions(Command.TReader.class)
	public String[] getNames() {
		return new String[] { lhsName, rhsName, destName };
	}

	@Override
	@ExecPermissions(TVisitor.class)
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
