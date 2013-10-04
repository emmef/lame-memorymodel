package org.emmef.threadingscenarios.lockfreeconfig;

import java.util.EnumMap;
import java.util.Map;

import org.emmef.threadingscenarios.memorymodel.FullFence;
import org.emmef.threadingscenarios.memorymodel.MemoryModel;
import org.emmef.threadingscenarios.memorymodel.Value;
import org.emmef.threadingscenarios.util.Logger;

/**
 * Don't do this at home. 
 */
public class SharedConfiguration {
	enum Selector {
		ODD, EVEN;
		
		public static Selector next(Selector selector) {
			return selector == Selector.EVEN ? Selector.ODD : Selector.EVEN;
		}
	}
	
	private Value<Selector> toReadFrom = new Value<>("toReadFrom", Selector.ODD);
	private Value<Selector> readFrom = new Value<>("readFrom", null);
	
	private final Map<Selector, String> configuration = new EnumMap<Selector, String>(Selector.class);
	
	public SharedConfiguration(String initialConfig) {
		for (Selector s : Selector.values()) {
			configuration.put(s, initialConfig);
		}
	}

	public String getConfiguration(long simulatedDuration) {
		String result;
		try (FullFence fence = new FullFence()) {
			Selector selector = toReadFrom.get();
			result = configuration.get(selector);
			Logger.log("Read config (from %s) as %s", selector, result);
			keepBusy(simulatedDuration);
			readFrom.set(selector);
		}
		return result;
	}
	
	public boolean setConfiguration(String conf, boolean wait) {
		MemoryModel.load();
		Selector readingFrom = toReadFrom.get();
		Selector writeTo = Selector.next(readingFrom);
		Logger.log("Setting configuration (in %s) to %s", writeTo, conf);
		configuration.put(writeTo, conf);
		Selector finishedReading = readFrom.get();
		
		while (finishedReading != readingFrom) {
			if (!wait) {
				return false;
			}
			Thread.yield();
			MemoryModel.load();
			finishedReading = readFrom.get();
		}
		toReadFrom.set(writeTo);
		MemoryModel.store();
		
		return true;
	}

	public static void keepBusy(long millisCount) {
		long t2 = System.currentTimeMillis();
		long t1;
		while ((t1 = System.currentTimeMillis()) == t2); // sync
		while ((t2 = System.currentTimeMillis()) - t1 < millisCount);
	}
}
