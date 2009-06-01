package ru.spbu.math.m04eiv.maths.server.matrix;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatrixPool {

	public final class Lock {
		private final MatrixDescriptor[] readLock;
		private final MatrixDescriptor[] writeLock;

		private final boolean acquired;

		private volatile boolean locked;

		private Lock(String[] sReadLock, String[] sWriteLock) {
			readLock = new MatrixDescriptor[sReadLock.length];
			writeLock = new MatrixDescriptor[sWriteLock.length];

			synchronized (map) {
				for (int i = 0; i < sReadLock.length; ++i) {
					readLock[i] = map.get(sReadLock[i]);
				}

				for (int i = 0; i < sWriteLock.length; ++i) {
					writeLock[i] = map.get(sWriteLock[i]);
				}

				for (int i = 0; i < readLock.length; ++i) {
					if (readLock[i] != null
							&& !readLock[i].getLock().readLock().tryLock()) {
						readUnlock(i);
						acquired = false;
						return;
					}
				}

				for (int i = 0; i < writeLock.length; ++i) {
					if (writeLock[i] != null
							&& !writeLock[i].getLock().writeLock().tryLock()) {
						readUnlock(readLock.length);
						writeUnlock(i);
						acquired = false;
						return;
					}
				}

				Map<String, MatrixDescriptor> addon = new HashMap<String, MatrixDescriptor>();
				for (int i = 0; i < readLock.length; ++i) {
					if (readLock[i] == null) {
						readLock[i] = new MatrixDescriptor();
						readLock[i].getLock().readLock().lock();
						addon.put(sReadLock[i], readLock[i]);
					}
				}

				for (int i = 0; i < writeLock.length; ++i) {
					if (writeLock[i] == null) {
						writeLock[i] = new MatrixDescriptor();
						writeLock[i].getLock().writeLock().lock();
						addon.put(sWriteLock[i], writeLock[i]);
					}
				}

				map.putAll(addon);
				locked = true;
				acquired = true;
			}
		}

		private void release() {
			locked = false;
			synchronized (map) {
				readUnlock(readLock.length);
				writeUnlock(writeLock.length);
			}
		}

		public MatrixDescriptor getReadDescriptor(int index) {
			assert acquired && locked;

			return readLock[index];
		}

		public MatrixDescriptor getWriteDescriptor(int index) {
			assert acquired && locked;

			return writeLock[index];
		}

		public boolean isAcquired() {
			return acquired;
		}

		/**
		 * MUST be called inside sync(map)
		 * 
		 * @param readLock
		 * @param count
		 */
		private void readUnlock(int count) {
			for (int j = 0; j < count; ++j) {
				if (readLock[j] != null) {
					readLock[j].getLock().readLock().unlock();
					readLock[j] = null;
				}
			}
		}

		/**
		 * MUST be called inside sync(map)
		 * 
		 * @param writeLock
		 * @param count
		 */
		private void writeUnlock(int count) {
			for (int j = 0; j < count; ++j) {
				if (writeLock[j] != null) {
					writeLock[j].getLock().writeLock().unlock();
					writeLock[j] = null;
				}
			}
		}
	}

	private final ConcurrentHashMap<String, MatrixDescriptor> map = new ConcurrentHashMap<String, MatrixDescriptor>();

	public Lock tryAcquireLock(String[] readLock, String[] writeLock) {
		return new Lock(readLock, writeLock);
	}

	public void releaseLock(Lock lock) {
		lock.release();
	}

}
