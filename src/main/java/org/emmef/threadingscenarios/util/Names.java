package org.emmef.threadingscenarios.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.collect.ImmutableList;

public interface Names {
	List<String> NAMES = ImmutableList.of("Ape", "Note", "Misere", "Wim", "Sister", "Jet", "Engine", "Soluken");
	
	class Generator implements Iterable<String> {
		@Override
		public Iterator<String> iterator() {
			return new Iterator<String>() {
				private final List<String> names = new ArrayList<>(NAMES);
				String name;
				
				@Override
				public boolean hasNext() {
					if (name != null) {
						return true;
					}
					if (names.isEmpty()) {
						return false;
					}
					int index = (int)(Math.random() * names.size());
					name = names.remove(index);
					
					return true;
				}
				
				@Override
				public String next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}
					String result = name;
					name = null;
					return result;
				}
				
				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}
}
