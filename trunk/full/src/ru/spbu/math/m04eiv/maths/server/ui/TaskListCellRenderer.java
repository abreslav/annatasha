package ru.spbu.math.m04eiv.maths.server.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import ru.spbu.math.m04eiv.maths.server.processor.Listener;
import ru.spbu.math.m04eiv.maths.server.ui.TasksListModel.TaskInfo;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public class TaskListCellRenderer extends JLabel implements ListCellRenderer {

	@Override
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
