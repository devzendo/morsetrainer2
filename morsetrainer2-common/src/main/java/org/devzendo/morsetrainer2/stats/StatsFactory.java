package org.devzendo.morsetrainer2.stats;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
			public Double getMorseCharacterSuccessPercentage(final MorseCharacter ch) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void recordMorseCharacterPerformance(final LocalDateTime now, final Double percentage) {
				// TODO Auto-generated method stub

			}

			@Override
			public List<MorseCharacterStat> getStatisticsSortedByAccuracy(final Set<MorseCharacter> morseCharactersSent) {
				// TODO Auto-generated method stub
				/*
				//final Double averagePercentage = morseCharactersSent.stream().map(ch -> statsStore.getMorseCharacterSuccessPercentage(ch)).collect(Collectors.averagingInt(i -> i));
				final Map<MorseCharacter, Integer> percentMap = new HashMap<>();
				for (final MorseCharacter ch: morseCharactersSent) {
					final Integer percentage = statsStore.getMorseCharacterSuccessPercentage(ch);
					percentMap.put(ch, percentage);
				}
				final List<MorseCharacter> sortedKeys = new ArrayList<>(percentMap.keySet());
				sortedKeys.sort(new Comparator<MorseCharacter>() {
					@Override
					public int compare(final MorseCharacter o1, final MorseCharacter o2) {
						if (percentMap.get(o1) < percentMap.get(o2))
						return 0;
					}});
				*/
				return Collections.emptyList();
			}};
	}
}
