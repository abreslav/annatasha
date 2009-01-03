package ru.spbu.math.m04eiv.maths.processor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.spbu.math.m04eiv.maths.tasks.ITask;

public abstract class Task implements ITask {
	
	private final WorkersManager manager;
	private final Listener listener;
	
	private final Lock executingLock;
	private final Condition finishedCondition;
	private volatile boolean interrupted;
	private volatile boolean finished;
	private final String description;
	
	public Task(WorkersManager manager, String description) {
		this.manager = manager;
		this.description = description;
		this.listener = manager.getListener();
		this.executingLock = new ReentrantLock();
		this.finishedCondition = this.executingLock.newCondition();
		this.interrupted = false;
		this.finished = false;
	}
	
	public final String getDescription() {
		return description;
	}
	
	protected final void enqueueWorker(Worker worker) {
		manager.enqueueWorker(worker);
	}
	
	public final boolean isInterrupted() {
		return interrupted;
	}
	
	protected final Listener getListener() {
		return listener;
	}
	
	public final void stop() {
		try {
			interrupted = true;
			interrupt();
		} finally {
			done(false);		
		}
		getListener().taskProgress(this, Listener.INTERRUPTED);
	}
	
	private final void done(boolean ok) {
		executingLock.lock();
		try {
			finished = true;
			finishedCondition.signalAll();
		} finally {
			executingLock.unlock();
		}
		
		if (ok) {
			getListener().taskProgress(this, Listener.DONE);
		}
	}
	
	public final void done() {
		done(true);
	}
	
	public final void join() {
		executingLock.lock();
		try {
			while (!finished) {
				try {
					finishedCondition.await();
				} catch (InterruptedException e) {}
			}
		} finally {
			executingLock.unlock();
		}
	}



}
