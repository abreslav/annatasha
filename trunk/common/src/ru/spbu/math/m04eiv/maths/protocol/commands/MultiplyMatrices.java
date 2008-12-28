package ru.spbu.math.m04eiv.maths.protocol.commands;


public final class MultiplyMatrices extends Command {

	private final String lhsName;
	private final String rhsName;
	private final String destName;

	public MultiplyMatrices(String lhsName, String rhsName, String destName) {
		this.lhsName = lhsName;
		this.rhsName = rhsName;
		this.destName = destName;
	}

	public String[] getNames() {
		return new String[] { lhsName, rhsName, destName };
	}

	@Override
	public void acceptVisitor(CommandsVisitor visitor) {
		visitor.visit(this);
	}

}
