package org.emmef.threadingscenarios.memorymodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class GetterSetter {
	private final Map<Object, Object> values = new HashMap<>();
	private final Set<Object> modifiedKeys = new HashSet<>();
	
	void adopt(Map<Object, Object> mainMemoryMap) {
		values.clear();
		values.putAll(mainMemoryMap);
		modifiedKeys.clear();
	}
	
	void copyTo(Map<Object, Object> mainMemoryMap) {
		StringBuilder written = new StringBuilder();
		for (Object key : modifiedKeys) {
			mainMemoryMap.put(key, values.get(key));
			written.append(", ").append(key);
		}
		modifiedKeys.clear();
	}
	
	void set(Object key, Object value) {
		values.put(key, value);
		modifiedKeys.add(key);
	}
	
	Object get(Object key) {
		Object value = values.get(key);
		return value;
	}
}
