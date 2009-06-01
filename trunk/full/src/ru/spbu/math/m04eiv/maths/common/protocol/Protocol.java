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

package ru.spbu.math.m04eiv.maths.common.protocol;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.spbu.math.m04eiv.maths.common.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.common.protocol.serialize.commands.CommandRepresentation;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public class Protocol {

	private final Socket socket;
	private final ICommandRunner commandRunner;
	private final ExecutorService executor;

	private class ListenerThread implements Runnable, TCommandsProcessor {

		@Override
		@ExecPermissions(TCommandsProcessor.class)
		public void run() {
			for (;;) {
				Command command;
				try {
					command = CommandRepresentation.getInstance()
							.readFromStream(socket.getInputStream());
					commandRunner.push(command);
				} catch (IOException e) {
				}
			}
		}

	}

	public Protocol(Socket socket, ICommandRunner commandRunner) {
		this.socket = socket;
		this.commandRunner = commandRunner;
		this.executor = Executors.newSingleThreadExecutor();
	}

	@ExecPermissions(TProtocolRunner.class)
	public void start() {
		executor.execute(new ListenerThread());
	}

	public void writeCommand(Command command) {
		try {
			CommandRepresentation.getInstance().writeToStream(command,
					socket.getOutputStream());
		} catch (IOException e) {
		}
	}

}
