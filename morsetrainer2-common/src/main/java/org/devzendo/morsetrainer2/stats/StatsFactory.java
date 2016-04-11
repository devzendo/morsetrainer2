package org.devzendo.morsetrainer2.stats;

import java.io.File;

public class StatsFactory {
	private final File storeDir;

	public StatsFactory(final File storeDir) {
		this.storeDir = storeDir;
	}

	public StatsStore open() {
		return new DatabaseStatsStore(storeDir);
	}
}
