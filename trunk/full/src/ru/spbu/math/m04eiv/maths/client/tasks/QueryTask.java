package ru.spbu.math.m04eiv.maths.client.tasks;

import ru.spbu.math.m04eiv.maths.common.protocol.Protocol;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.tasks.ITask;

final class QueryTask implements ITask {

	private final Protocol protocol;
	private final Command command;

	public QueryTask(Protocol protocol, Command command) {
		this.protocol = protocol;
		this.command = command;
	}

	@Override
	public void execute() {
		protocol.writeCommand(command);
	}

	@Override
	public void interrupt() {
	}

	@Override
	public void releaseResources() {
	}

	@Override
	public boolean tryFetchResources() {
		return true;
	}

	@Override
	public void join() {}

}
