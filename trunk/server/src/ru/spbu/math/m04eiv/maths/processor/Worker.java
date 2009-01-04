package ru.spbu.math.m04eiv.maths.processor;

import com.google.code.annatasha.annotations.ThreadMarker;
import com.google.code.annatasha.annotations.Method.ExecPermissions;

@ThreadMarker
public interface Worker extends Runnable {
	
	@Override
	@ExecPermissions(Worker.class)
	public void run();
	
}
