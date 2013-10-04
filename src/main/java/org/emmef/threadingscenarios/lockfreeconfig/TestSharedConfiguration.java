package org.emmef.threadingscenarios.lockfreeconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import org.emmef.threadingscenarios.util.Logger;

import com.google.common.collect.ImmutableList;

public class TestSharedConfiguration {
	private static final ImmutableList<String> CONFIGURATION_SEQUENCE = ImmutableList.of("Ape", "Note", "Misere", "Wim", "Sister", "Jet", "Engine");
	private final SharedConfiguration sharedConfiguration = new SharedConfiguration("Initial");
	private volatile long configFinishedStamp = -1L;
	CountDownLatch readerStarted = new CountDownLatch(1);
	CountDownLatch readerStopped = new CountDownLatch(1);
	List<String> configurationSequence = new ArrayList<>(); 
	
	Thread configThread = new Thread("Config") {
		public void run() {
			try {
				Logger.log("Waiting for reader thread to start");
				readerStarted.await();
				Logger.log("Reader thread started!");
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (String name : CONFIGURATION_SEQUENCE) {
				sharedConfiguration.setConfiguration(name, true);
				SharedConfiguration.keepBusy((long)(Math.random() * 1000));
			}
			configFinishedStamp = System.currentTimeMillis();
		}
	};
	
	Thread readerThread = new Thread("Reader") {
		public void run() {
			String previousRead = null;
			long startTime = System.currentTimeMillis();
			try {
				while (configFinishedStamp == -1 || System.currentTimeMillis() - configFinishedStamp < 1000) {
					String config = sharedConfiguration.getConfiguration(10);
					if (!Objects.equals(previousRead, config)) {
						Logger.log("Configuration changed from %s to %s", previousRead, config);
						configurationSequence.add(config);
						previousRead = config; 
					}
					SharedConfiguration.keepBusy(10);
					
					if (System.currentTimeMillis() - startTime > 1000) {
						readerStarted.countDown();
					}
				}
			}
			finally {
				readerStopped.countDown();
			}
		}
	};
	
	TestSharedConfiguration() {
		readerThread.start();
		configThread.start();
	}
	
	List<String> getConfigurationSequence() {
		try {
			readerStopped.await();
			
			return configurationSequence; 
		} 
		catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static void main(String[] args) {
		TestSharedConfiguration testSharedConfiguration = new TestSharedConfiguration();
		
		List<String> sequence = testSharedConfiguration.getConfigurationSequence();
		
		if (!Objects.equals(CONFIGURATION_SEQUENCE, sequence)) {
			System.err.println("Configuration sequence incorrect. \nExpected: " + CONFIGURATION_SEQUENCE + "\nActual:   " + sequence);
		}
		else {
			System.out.println("Configuration sequence successfully copied\nSequence: " + sequence);
		}
	}
}
