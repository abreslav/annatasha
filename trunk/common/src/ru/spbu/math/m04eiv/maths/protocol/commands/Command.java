package ru.spbu.math.m04eiv.maths.protocol.commands;

public abstract class Command {

	public abstract void acceptVisitor(CommandsVisitor visitor);
	
}
