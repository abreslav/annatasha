package ru.spbu.math.m04eiv.maths.protocol.serialize;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import ru.spbu.math.m04eiv.maths.protocol.commands.Command;
import ru.spbu.math.m04eiv.maths.protocol.serialize.commands.CommandRepresentation;

public class Serializer {
	
	private final static CommandRepresentation commandRepresentation = new CommandRepresentation();
	
	public static void read(InputStream is, byte[] array, int offs, int len) throws IOException {
		assert is != null;
		assert array != null;
		assert (0 <= offs) && (len >= 0) && (offs + len < array.length);
		
		int rd = 0;
		do {
			final int r = is.read(array, offs + rd, len - rd);
			if (r == -1) {
				throw new EOFException();
			}
			rd += r;
		} while (rd < len);
	}
	
	public static void read(InputStream is, byte[] array) throws IOException {
		read(is, array, 0, array.length);
	}
	
	public static void write(OutputStream os, byte[] array, int offs, int len) throws IOException {
		assert os != null;
		assert array != null;
		assert (0 <= offs) && (len >= 0) && (offs + len < array.length);
		
		os.write(array, offs, len);
	}
	
	public static void write(OutputStream os, byte[] array) throws IOException {
		write(os, array, 0, array.length);
	}

	public static Command readCommand(Socket socket) throws IOException {
		assert socket != null;
		
		return commandRepresentation.readFromStream(socket.getInputStream());
	}
	
	public static void writeCommand(Command command, Socket socket) throws IOException {
		assert socket != null;
		assert command != null;
		
		commandRepresentation.writeToStream(command, socket.getOutputStream());
	}

}
