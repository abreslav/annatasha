package ru.spbu.math.m04eiv.maths.protocol.commands;

import com.google.code.annatasha.annotations.ThreadMarker;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

public abstract class Command {
	

	@ThreadMarker
	public interface Constructor {}
	
	@ThreadMarker
	public interface Visitor {}
	
	@ThreadMarker
	public interface Reader extends Visitor {}

	@ThreadMarker
	public interface Writer {}
	
	@ExecPermissions(Visitor.class)
	public abstract void acceptVisitor(CommandsVisitor visitor);
	
}
