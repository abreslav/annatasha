package ru.spbu.math.m04eiv.maths.processor;

public interface Listener {

	public static final int DONE = -2;
	public static final int INTERRUPTED = -1;

	public void taskStarted(Task task, int workersCount);

	public void taskProgress(Task task, int done);

}
