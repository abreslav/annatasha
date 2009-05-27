package ru.spbu.math.m04eiv.maths.protocol;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

import ru.spbu.math.m04eiv.maths.protocol.commands.Command;

public interface ICommandRunner {

	@ExecPermissions(Command.TVisitor.class)
	void push(Command command);

}