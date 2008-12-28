package ru.spbu.math.m04eiv.maths.tasks;

public interface ITask {
	public abstract boolean tryFetchResources();
	public abstract void releaseResources();
	
	public abstract void execute();
	public abstract void interrupt();
}
