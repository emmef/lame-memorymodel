package org.emmef.threadingscenarios.memorymodel;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;
/**
 * Describes a value that is thread-local and needs to be explicitly stored or loaded to be visible to other threads.
 * 
 * @see MemoryModel#load()
 * @see MemoryModel#store()
 */
public final class Value<V> {
	private static final Set<Object> keys = new HashSet<>();
	
	private final Object key;
	
	public Value(Object key, V value) {
		checkNotNull(key, "key");
		
		synchronized (key) {
			if (keys.contains(key)) {
				throw new IllegalArgumentException("There is already a " + Value.class.getSimpleName() + " that uses key " + key);
			}
			keys.add(key);
			this.key = key;
		}
		set(value);
	}
	
	public Value() {
		this(new Object(), null);
	}
	
	public Value(V value) {
		this(new Object(), value);
	}
	
	public final void set(V value) {
		MemoryModel.THREAD_MEMORY.get().set(key, value);
	}
	
	public final V get() {
		@SuppressWarnings("unchecked")
		V value = (V)MemoryModel.THREAD_MEMORY.get().get(key);
		return value;
	}
}