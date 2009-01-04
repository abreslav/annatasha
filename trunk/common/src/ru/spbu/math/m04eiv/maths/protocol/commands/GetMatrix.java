package ru.spbu.math.m04eiv.maths.protocol.commands;

import com.google.code.annatasha.annotations.Field.ReadPermissions;
import com.google.code.annatasha.annotations.Field.WritePermissions;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

public class GetMatrix extends Command {

	@ReadPermissions(Command.Reader.class)
	@WritePermissions(Command.Constructor.class)
	private final int uid;

	@ReadPermissions(Command.Reader.class)
	@WritePermissions(Command.Constructor.class)
	private final String name;

	@ExecPermissions(Command.Constructor.class)
	public GetMatrix(int uid, String name) {
		this.uid = uid;
		this.name = name;
	}

	@ExecPermissions(Command.Reader.class)
	public String getName() {
		return name;
	}
	
	@ExecPermissions(Command.Reader.class)
	public int getUid() {
		return uid;
	}

	@Override
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
