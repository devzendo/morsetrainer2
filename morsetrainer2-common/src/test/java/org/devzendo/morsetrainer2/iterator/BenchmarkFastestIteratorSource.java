package org.devzendo.morsetrainer2.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BenchmarkFastestIteratorSource {

	public void linkedList() {
		benchmark(new LinkedList<>());
	}

	public void arrayList() {
		benchmark(new ArrayList<>());
	}

	private void benchmark(final List<Integer> ll) {
		for (int i=0; i < 100000; i++) {
			ll.add(i);
		}
		final long start = System.currentTimeMillis();
		long total = 0L;
		for (int i=0; i < 100000; i++) {
			final Iterator<Integer> it = ll.iterator();
			while (it.hasNext()) {
				final Integer next = it.next();
				total += next;
			}
		}
		final long stop = System.currentTimeMillis();
		System.out.println("Total for " + ll.getClass().getSimpleName() + " is " + total + " in " + (stop - start) + " ms");
	}

}
