package ru.spbu.math.m04eiv.maths.protocol.commands;

import com.google.code.annatasha.annotations.ThreadMarker;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

public abstract class Command {

	@ThreadMarker
	public interface TConstructor {}
	
	@ThreadMarker
	public interface TVisitor {}
	
	@ThreadMarker
	public interface TReader extends TVisitor {}

	@ThreadMarker
	public interface TWriter {}
	
	@ExecPermissions(TVisitor.class)
	public abstract void acceptVisitor(CommandsVisitor visitor);
	
}
