package org.emmef.threadingscenarios.memorymodel;

import java.util.HashMap;
import java.util.Map;

/**
 * Simulates a memory model, where different {@link Value}s are ensured 
 * not to see each others values in different threads, unless forced by 
 * either {@link #load()} or {@link #store()}.
 * 
 *  @see Value
 */
public class MemoryModel {
	private static final Map<Object, Object> MAIN_MEMORY = new HashMap<>();
	private static final Object MEMORY_LOCK = new Object();
	
	static final ThreadLocal<GetterSetter> THREAD_MEMORY = new ThreadLocal<GetterSetter>() {
		protected GetterSetter initialValue() {
			return new GetterSetter();
		};
	};
	
	/**
	 * Loads all {@link Value}s from main memory.
	 * <p>
	 * This always overwrites all values that where not previously 
	 * stored by any thread: including this one. That is perhaps a 
	 * little too strict, but forces the user to really think about 
	 * what to write and what not to.
	 */
	public static void load() {
		synchronized (MEMORY_LOCK) {
			THREAD_MEMORY.get().adopt(MAIN_MEMORY);
		}
	}
	
	/**
	 * Stores all {@link Value}s in the current thread to the simulated main memory.
	 * <p>
	 * Only changes that where made between the last call to {@link #load()}
	 * or {@link #store()} are actually written to main memory. This means that 
	 * calling this method twice in the same thread makes the second call 
	 * a no-op.  
	 */
	public static void store() {
		synchronized (MEMORY_LOCK) {
			THREAD_MEMORY.get().copyTo(MAIN_MEMORY);
		}
	}
}
