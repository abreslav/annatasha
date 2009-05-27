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

package ru.spbu.math.m04eiv.maths.protocol;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

import ru.spbu.math.m04eiv.maths.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.protocol.serialize.commands.CommandRepresentation;

public class Protocol {

	private final Socket socket;
	private final ICommandRunner commandRunner;
	private final ExecutorService executor;

	private class ListenerThread implements Runnable, Command.TConstructor {

		@Override
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

	public void start() {
		executor.execute(new ListenerThread());
	}

	@ExecPermissions(Command.TWriter.class)
	public void writeCommand(Command command) {
		try {
			CommandRepresentation.getInstance().writeToStream(command,
					socket.getOutputStream());
		} catch (IOException e) {
		}
	}

}
