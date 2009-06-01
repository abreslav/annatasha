package ru.spbu.math.m04eiv.maths.client;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;

import ru.spbu.math.m04eiv.maths.client.tasks.TasksFactory;
import ru.spbu.math.m04eiv.maths.common.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.common.protocol.ICommandRunner;
import ru.spbu.math.m04eiv.maths.common.protocol.Protocol;
import ru.spbu.math.m04eiv.maths.common.protocol.TProtocolRunner;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.GetMatrix;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.MultiplyMatrices;
import ru.spbu.math.m04eiv.maths.common.protocol.commands.SetMatrix;
import ru.spbu.math.m04eiv.maths.common.tasks.ITask;
import ru.spbu.math.m04eiv.maths.common.tasks.TTaskExecutor;

import com.google.code.annatasha.annotations.Method.ExecPermissions;

public final class Client implements Runnable, TProtocolRunner {
	private final class ClientTasksRunner implements ICommandRunner,
			TTaskExecutor {
		private final TasksFactory factory;

		private ClientTasksRunner(TasksFactory factory) {
			this.factory = factory;
		}

		@Override
		@ExecPermissions(TTaskExecutor.class)
		public void push(Command command) {
			ITask task = factory.createTask(Client.this.proto, command);
			task.execute();
		}
	}

	private final static int PORT = 4848;

	private volatile Protocol proto;

	public Client() {
	}

	@ExecPermissions(TProtocolRunner.class)
	public void run() {
		try {
			Socket socket = new Socket("localhost", PORT);
			final TasksFactory factory = new TasksFactory();

			Protocol proto = new Protocol(socket,
					new ClientTasksRunner(factory));
			this.proto = proto;
			proto.start();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private final static int SZ = 1000;

	public static void main(String[] args) {
		Client client = new Client();
		Executors.newSingleThreadExecutor().execute(client);
		while (client.proto == null)
			;
		Matrix m_a = new Matrix(SZ, SZ);
		m_a.setCell(0, 0, 1);
		m_a.setCell(0, 1, 2);
		m_a.setCell(0, 2, 3);
		m_a.setCell(1, 0, 4);
		m_a.setCell(1, 1, 5);
		m_a.setCell(1, 2, 6);
		client.proto.writeCommand(new SetMatrix("a", m_a));
		client.proto.writeCommand(new GetMatrix(0, "a"));

		Matrix m_b = new Matrix(SZ, SZ);
		m_b.setCell(0, 0, 1);
		m_b.setCell(1, 0, 2);
		m_b.setCell(2, 0, 5);

		client.proto.writeCommand(new SetMatrix("b", m_b));
		client.proto.writeCommand(new GetMatrix(1, "b"));

		client.proto.writeCommand(new MultiplyMatrices("a", "b", "c"));
		client.proto.writeCommand(new GetMatrix(2, "c"));

	}

}
