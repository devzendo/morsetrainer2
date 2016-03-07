package org.devzendo.morsetrainer2.stats;

import java.io.File;
import java.time.LocalDateTime;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;

public class StatsFactory {
	private final File storeDir;

	public StatsFactory(final File storeDir) {
		this.storeDir = storeDir;
	}

	public StatsStore open() {
		return new StatsStore() {

			@Override
			public void incrementWordLengthSentCount(final int length) {
				// TODO Auto-generated method stub

			}

			@Override
			public void incrementWordLengthSuccessCount(final int length) {
				// TODO Auto-generated method stub

			}

			@Override
			public void incrementSentCount(final MorseCharacter mc) {
				// TODO Auto-generated method stub

			}

			@Override
			public void incrementSuccessfulDecodeCount(final MorseCharacter ch) {
				// TODO Auto-generated method stub

			}

			@Override
			public Integer getWordLengthSuccessPercentage(final Integer wordLength) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void recordWordLengthPerformance(final LocalDateTime now, final Integer percentage) {
				// TODO Auto-generated method stub

			}

			@Override
			public Integer getMorseCharacterSuccessPercentage(final MorseCharacter ch) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void recordMorseCharacterPerformance(final LocalDateTime now, final Integer percentage) {
				// TODO Auto-generated method stub

			}};
	}
}
