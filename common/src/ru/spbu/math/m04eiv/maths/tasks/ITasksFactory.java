package ru.spbu.math.m04eiv.maths.tasks;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

import ru.spbu.math.m04eiv.maths.protocol.Protocol;
import ru.spbu.math.m04eiv.maths.protocol.commands.Command;

public interface ITasksFactory {
	
	public interface TaskCreator extends Command.Reader {}

	@ExecPermissions(TaskCreator.class)
	ITask createTask(Protocol protocol, Command command);

}