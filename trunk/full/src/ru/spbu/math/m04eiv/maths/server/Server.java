package ru.spbu.math.m04eiv.maths.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ru.spbu.math.m04eiv.maths.common.protocol.Protocol;
import ru.spbu.math.m04eiv.maths.server.matrix.MatrixPool;
import ru.spbu.math.m04eiv.maths.server.processor.TServer;
import ru.spbu.math.m04eiv.maths.server.processor.WorkersManager;
import ru.spbu.math.m04eiv.maths.server.protocol.CommandRunner;
import ru.spbu.math.m04eiv.maths.server.tasks.TasksFactory;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public final class Server implements Runnable, TServer {

	private final static int PORT = 4848;

	private final WorkersManager manager;
	private final MatrixPool pool;

	private final TasksFactory tasksFactory;

	public Server() {
		this.manager = new WorkersManager();
		this.pool = new MatrixPool();
		this.tasksFactory = new TasksFactory(manager, pool);
	}

	@ExecPermissions(TServer.class)
	public void run() {
		try {
			manager.start();
			ServerSocket serverSocket = new ServerSocket(PORT);
			while (true) {
				Socket socket = serverSocket.accept();
				Protocol proto = newConnection(socket);
				proto.start();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * @param args
	 */
	@ExecPermissions(TServer.class)
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public WorkersManager getManager() {
		return manager;
	}

	private Protocol newConnection(Socket socket) {
		CommandRunner commandRunner = new CommandRunner(manager, tasksFactory);
		Protocol protocol = new Protocol(socket, commandRunner);
		commandRunner.setProtocol(protocol);

		return protocol;
	}

}
