package ru.spbu.math.m04eiv.maths.server.tasks;

import ru.spbu.math.m04eiv.maths.common.tasks.ITask;
import ru.spbu.math.m04eiv.maths.server.protocol.TTaskProcessor;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public interface ITasksProcessor {

	@ExecPermissions(TTaskProcessor.class)
	public abstract void addTask(ITask task);

}