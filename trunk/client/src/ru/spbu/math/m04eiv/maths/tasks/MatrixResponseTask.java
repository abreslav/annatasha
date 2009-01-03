package ru.spbu.math.m04eiv.maths.tasks;

import ru.spbu.math.m04eiv.maths.matrix.Matrix;
import ru.spbu.math.m04eiv.maths.matrix.Matrix.Dimensions;
import ru.spbu.math.m04eiv.maths.protocol.Status;
import ru.spbu.math.m04eiv.maths.protocol.commands.MatrixResponse;
import ru.spbu.math.m04eiv.maths.tasks.ITask;

final class MatrixResponseTask implements ITask {
	
	private final MatrixResponse command;

	/**
	 * 
	 * @param man
	 * @param command
	 */
	public MatrixResponseTask(MatrixResponse command) {
		this.command = command;
	}

	@Override
	public void execute() {
		if (command.getStatus() == Status.Ready) {
			final Matrix matrix = command.getMatrix();
			final Dimensions dim = matrix.getSize();
			for (int m = 0; m < dim.M; ++m) {
				for (int n = 0; n < dim.N; ++n) {
					System.out.print("" + matrix.getCell(m, n) + " ");
				}
				System.out.println();
			}
		} else {
			System.out.println(command.getStatus());
		}
	}

	@Override
	public void interrupt() {}
	
	@Override
	public void join() {}
	
	@Override
	public void releaseResources() {
	}

	@Override
	public boolean tryFetchResources() {
		return true;
	}

}
