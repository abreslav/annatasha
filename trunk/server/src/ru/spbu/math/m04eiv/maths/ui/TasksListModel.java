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

package ru.spbu.math.m04eiv.maths.ui;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

import ru.spbu.math.m04eiv.maths.processor.Listener;
import ru.spbu.math.m04eiv.maths.processor.Task;
import ru.spbu.math.m04eiv.maths.processor.WorkersManager;
import ru.spbu.math.m04eiv.maths.tasks.TTaskManager;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public final class TasksListModel extends AbstractListModel {
	
	protected final static class TaskInfo {
		public Task task;
		public int full;
		public int done;
	}
	
	private final ArrayList<TaskInfo> tasks;
	
	private final ProcessorListener listener;
	
	public TasksListModel(WorkersManager manager) {
		tasks = new ArrayList<TaskInfo>();
		listener = new ProcessorListener();
		
		manager.setListener(listener);
	}

	@Override
	@ExecPermissions(UIRunnable.class)
	public Object getElementAt(int index) {
		return tasks.get(index);
	}

	@Override
	public int getSize() {
		return tasks.size();
	}
	
	private final class ProcessorListener implements Listener {

		@Override
		@ExecPermissions(TTaskManager.class)
		public void taskProgress(Task task, int done) {
			SwingUtilities.invokeLater(new TaskProgress(task, done));
		}

		@Override
		@ExecPermissions(TTaskManager.class)
		public void taskStarted(Task task, int workersCount) {
			final TaskInfo ti = new TaskInfo();
			ti.task = task;
			ti.done = 0;
			ti.full = workersCount;
			
			SwingUtilities.invokeLater(new TaskAdded(ti));
		}
		
	}
	
	private final class TaskAdded implements UIRunnable {

		private final TaskInfo taskInfo;

		public TaskAdded(TaskInfo taskInfo) {
			this.taskInfo = taskInfo;
		}

		@Override
		public void run() {
			final int oldSize = tasks.size();
			tasks.add(taskInfo);
			fireIntervalAdded(TasksListModel.this, oldSize, oldSize);
		}
		
	}
	
	private final class TaskProgress implements UIRunnable {

		private final Task task;
		private final int done;

		public TaskProgress(Task task, int done) {
			this.task = task;
			this.done = done;
		}

		@Override
		public void run() {
			int p = -1;
			for (int i = tasks.size(); i > 0;) {
				final TaskInfo ti = tasks.get(--i);
				if (ti.task == task) {
					p = i;
					break;
				}
			}
			if (p != -1) {
				tasks.get(p).done = done;
			}
			fireContentsChanged(TasksListModel.this, p, p);
		}
		
	}
}
