package org.emmef.threadingscenarios.util;

import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.Objects;

public class Logger {
	private static final AtomicLong logCount = new AtomicLong();
	private static final long startTime = System.currentTimeMillis();
	private static ThreadLocal<String> lastLine = new ThreadLocal<>();
	
	public static void log(String string) {
		outPrintln(formatLogLine(string));
	}

	public static void log(String string, Object... objects) {
		outPrintln(formatLogLine(String.format(string, objects)));
	}
	
	public static void err(String string) {
		errPrintln(formatLogLine(string));
	}
	
	public static void err(String string, Object... objects) {
		errPrintln(formatLogLine(String.format(string, objects)));
	}

	private static String formatLogLine(String string) {
		long count = logCount.getAndIncrement();
		long upTime = System.currentTimeMillis() - startTime;
		
		String previous = lastLine.get();
		if (Objects.equal(previous, string)) {
			return null;
		}
		else {
			lastLine.set(string);
		}
		return String.format("%08d | time=%07d\t| thread=%s | %s", count, upTime, Thread.currentThread().getName(), string);
	}
	
	private static void errPrintln(String formatLogLine) {
		if (formatLogLine != null) {
			System.err.println(formatLogLine);
		}
	}
	
	private static void outPrintln(String formatLogLine) {
		if (formatLogLine != null) {
			System.out.println(formatLogLine);
		}
	}
}
