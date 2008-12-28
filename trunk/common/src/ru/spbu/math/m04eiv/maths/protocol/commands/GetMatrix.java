package ru.spbu.math.m04eiv.maths.protocol.commands;


public class GetMatrix extends Command {

	private final int uid;
	private final String name;

	public GetMatrix(int uid, String name) {
		this.uid = uid;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public int getUid() {
		return uid;
	}

	@Override
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
