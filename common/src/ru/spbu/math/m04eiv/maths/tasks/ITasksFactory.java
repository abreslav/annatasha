package ru.spbu.math.m04eiv.maths.tasks;

import ru.spbu.math.m04eiv.maths.protocol.Protocol;
import ru.spbu.math.m04eiv.maths.protocol.commands.Command;

public interface ITasksFactory {

	ITask createTask(Protocol protocol, Command command);

}
