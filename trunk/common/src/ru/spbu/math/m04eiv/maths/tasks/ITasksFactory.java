package ru.spbu.math.m04eiv.maths.tasks;

import com.google.code.annatasha.annotations.ThreadMarker;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

import ru.spbu.math.m04eiv.maths.protocol.Protocol;
import ru.spbu.math.m04eiv.maths.protocol.commands.Command;

public interface ITasksFactory {
	
	@ThreadMarker
	public interface TTaskCreator extends Command.TReader {}

	@ExecPermissions(TTaskCreator.class)
	ITask createTask(Protocol protocol, Command command);

}
