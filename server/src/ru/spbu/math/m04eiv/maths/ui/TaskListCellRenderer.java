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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import ru.spbu.math.m04eiv.maths.processor.Listener;
import ru.spbu.math.m04eiv.maths.ui.TasksListModel.TaskInfo;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public class TaskListCellRenderer extends JLabel implements ListCellRenderer {

	@Override
	@ExecPermissions(UIRunnable.class)
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		assert list != null;
		assert value != null;

		final TaskInfo taskInfo = (TaskInfo) value;

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		final int f = taskInfo.full;
		final int d = taskInfo.done;
		final String desc = taskInfo.task.getDescription();

		switch (d) {
		case Listener.INTERRUPTED:
			setText(desc + ": INTERRUPTED");
			break;

		case Listener.DONE:
			setText(desc + ": DONE");
			break;

		default:
			setText(desc + ": " + (int) ((float) d / f * 100) + "%");
			break;
		}
		return this;
	}

}
