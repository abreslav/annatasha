package ru.spbu.math.m04eiv.maths.protocol.commands;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public abstract class Command {
	
	public interface Constructor {}
	public interface Reader {}
	
	@ExecPermissions(Reader.class)
	public abstract void acceptVisitor(CommandsVisitor visitor);
	
}
