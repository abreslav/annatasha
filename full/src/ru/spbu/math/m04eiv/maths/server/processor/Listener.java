package ru.spbu.math.m04eiv.maths.server.processor;

import ru.spbu.math.m04eiv.maths.tasks.TTaskManager;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public interface Listener {

	public static final int DONE = -2;
	public static final int INTERRUPTED = -1;
	
	@ExecPermissions(TTaskManager.class)
	public void taskStarted(Task task, int workersCount);

	@ExecPermissions(TTaskManager.class)
	public void taskProgress(Task task, int done);

}
