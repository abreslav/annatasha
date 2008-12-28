package ru.spbu.math.m04eiv.maths.protocol;

import java.net.Socket;

public class ProtocolsManager {
	
	public Protocol newConnection(Socket socket, CommandRunnersManager commandRunnersManager) {
		CommandRunner commandRunner = commandRunnersManager.newCommandRunner();
		Protocol protocol = new Protocol(socket, commandRunner);
		commandRunner.setProtocol(protocol);
		
		return protocol;
	}

}
