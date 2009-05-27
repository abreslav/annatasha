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

package ru.spbu.math.m04eiv.maths.tasks;

import ru.spbu.math.m04eiv.maths.protocol.Protocol;
import ru.spbu.math.m04eiv.maths.protocol.commands.Command;

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
