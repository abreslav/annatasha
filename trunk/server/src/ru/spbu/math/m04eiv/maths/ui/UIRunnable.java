package ru.spbu.math.m04eiv.maths.ui;

import com.google.code.annatasha.annotations.ThreadMarker;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

@ThreadMarker
public interface UIRunnable extends Runnable {
	
	@Override
	@ExecPermissions(UIRunnable.class)
	public void run();

}
