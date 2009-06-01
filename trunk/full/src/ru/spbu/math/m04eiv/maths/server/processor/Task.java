package ru.spbu.math.m04eiv.maths.server.processor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.spbu.math.m04eiv.maths.common.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.tasks.ITask;
import ru.spbu.math.m04eiv.maths.tasks.TTaskManager;

import com.google.code.annatasha.annotations.ThreadMarker;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

public abstract class Task implements ITask {
	
	@ThreadMarker
	protected interface TExecutor extends Command.TWriter {}
	
	private final WorkersManager manager;
	private final Listener listener;
	
	private final Lock executingLock;
	private final Condition finishedCondition;
	private volatile boolean interrupted;
	private volatile boolean finished;
	private final String description;
	
	public Task(WorkersManager manager, String description) {
		System.err.println(description);
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
	
	@ExecPermissions(TTaskManager.class)
	public final void stop() {
		try {
			interrupted = true;
			interrupt();
		} finally {
			done(false);		
		}
		getListener().taskProgress(this, Listener.INTERRUPTED);
	}
	
	@Override
	@ExecPermissions(Task.TExecutor.class)
	public abstract void execute();
	
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
