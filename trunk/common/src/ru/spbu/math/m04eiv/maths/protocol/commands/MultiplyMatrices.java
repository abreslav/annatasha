package ru.spbu.math.m04eiv.maths.protocol.commands;

import com.google.code.annatasha.annotations.Field.ReadPermissions;
import com.google.code.annatasha.annotations.Field.WritePermissions;
import com.google.code.annatasha.annotations.Method.ExecPermissions;


public final class MultiplyMatrices extends Command {

	@ReadPermissions(Command.Reader.class)
	@WritePermissions(Command.Constructor.class)
	private final String lhsName;
	
	@ReadPermissions(Command.Reader.class)
	@WritePermissions(Command.Constructor.class)
	private final String rhsName;

	@ReadPermissions(Command.Reader.class)
	@WritePermissions(Command.Constructor.class)
	private final String destName;

	@ExecPermissions(Command.Constructor.class)
	public MultiplyMatrices(String lhsName, String rhsName, String destName) {
		this.lhsName = lhsName;
		this.rhsName = rhsName;
		this.destName = destName;
	}

	@ExecPermissions(Command.Reader.class)
	public String[] getNames() {
		return new String[] { lhsName, rhsName, destName };
	}

	@Override
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
