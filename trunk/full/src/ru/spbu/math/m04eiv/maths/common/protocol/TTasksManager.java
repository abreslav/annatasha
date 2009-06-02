package ru.spbu.math.m04eiv.maths.common.protocol;

import ru.spbu.math.m04eiv.maths.common.tasks.TTaskExecutor;
import ru.spbu.math.m04eiv.maths.common.tasks.TTasksFactory;

import com.google.code.annatasha.annotations.ThreadMarker;

@ThreadMarker
public interface TTasksManager extends TTasksFactory, TTaskExecutor {

}
