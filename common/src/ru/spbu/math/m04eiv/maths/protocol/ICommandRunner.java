package ru.spbu.math.m04eiv.maths.protocol;

import ru.spbu.math.m04eiv.maths.protocol.commands.Command;

public interface ICommandRunner {

	void push(Command command);

}