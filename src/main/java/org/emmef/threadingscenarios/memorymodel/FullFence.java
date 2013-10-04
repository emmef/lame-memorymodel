package org.emmef.threadingscenarios.memorymodel;

/**
 * Simulates a full memory fence: acquire at construction 
 * and release on {@link #close()}.
 */
public class FullFence implements AutoCloseable {
	public FullFence() {
		MemoryModel.load();
	}
	
	@Override
	public void close() {
		MemoryModel.store();
	}
}