/*******************************************************************************
 * Copyright (c) 2008, 2009 Ivan Egorov <egorich.3.04@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ivan Egorov <egorich.3.04@gmail.com>
 *******************************************************************************/

package ru.spbu.math.m04eiv.maths.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import ru.spbu.math.m04eiv.maths.matrix.TMatrixReader;
import ru.spbu.math.m04eiv.maths.matrix.TMatrixWriter;
import ru.spbu.math.m04eiv.maths.tasks.TResourceManager;
import ru.spbu.math.m04eiv.maths.tasks.ITask;
import ru.spbu.math.m04eiv.maths.tasks.ITasksProcessor;

public final class WorkersManager implements ITasksProcessor {

	private final class Dispatcher implements Runnable {
		@Override
		public void run() {
			for (;;) {
				try {
					final ITask task = tasks.take();
					tasksExecutor.execute(new TaskRunner(task));
				} catch (InterruptedException e) {
				}
			}

		}
	}

	private final class TaskRunner implements Runnable, TMatrixWriter,
			TMatrixReader, TResourceManager {
		private final ITask task;

		private TaskRunner(ITask task) {
			this.task = task;
		}

		@Override
		public void run() {
			if (task.tryFetchResources()) {
				try {
					task.execute();
					task.join();
				} finally {
					task.releaseResources();
				}
			} else {
				try {
					tasks.put(task);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private static final int MAX_WORKERS = 10;

	private final Executor executor = Executors.newFixedThreadPool(MAX_WORKERS);
	private final Executor tasksExecutor = Executors.newCachedThreadPool();

	private volatile Listener listener = DUMMY_LISTENER;

	private BlockingQueue<ITask> tasks = new LinkedBlockingQueue<ITask>();

	public void start() {
		executor.execute(new Dispatcher());
	}

	/* (non-Javadoc)
	 * @see ru.spbu.math.m04eiv.maths.processor.ITaskProcessor#addTask(ru.spbu.math.m04eiv.maths.tasks.ITask)
	 */
	public void addTask(ITask task) {
		assert task != null;

		try {
			tasks.put(task);
		} catch (InterruptedException e) {
		}
	}

	public Listener getListener() {
		Listener l = listener;
		if (l != null) {
			return l;
		} else {
			return DUMMY_LISTENER;
		}
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public void enqueueWorker(Worker worker) {
		executor.execute(worker);
	}

	private static final Listener DUMMY_LISTENER = new Listener() {

		@Override
		public void taskProgress(Task task, int left) {
		}

		@Override
		public void taskStarted(Task task, int workersCount) {
		}

	};

}
