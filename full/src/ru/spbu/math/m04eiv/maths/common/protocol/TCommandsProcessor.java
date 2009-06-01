package ru.spbu.math.m04eiv.maths.common.protocol;

import ru.spbu.math.m04eiv.maths.common.protocol.serialize.TBinaryStreamReader;

import com.google.code.annatasha.annotations.ThreadMarker;

@ThreadMarker
public interface TCommandsProcessor extends TBinaryStreamReader,
		TCommandsTasksFactory {

}
