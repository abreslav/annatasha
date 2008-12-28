package ru.spbu.math.m04eiv.maths.protocol;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.spbu.math.m04eiv.maths.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.protocol.serialize.Serializer;

public class Protocol {

	private final Socket socket;
	private final ICommandRunner commandRunner;
	private final ExecutorService executor;
	
	private class ClientThread implements Runnable {

		@Override
		public void run() {
			for (;;) {
				Command command;
				try {
					command = Serializer.readCommand(socket);
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
		executor.execute(new ClientThread());
	}
	
	public void writeCommand(Command command) {
		try {
			Serializer.writeCommand(command, socket);
		} catch (IOException e) {}
	}

}
