package org.devzendo.morsetrainer2.stats;

import java.time.LocalDateTime;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;

public interface StatsStore {

	void incrementWordLengthSentCount(int length);

	void incrementWordLengthSuccessCount(int length);

	void incrementSentCount(MorseCharacter mc);

	void incrementSuccessfulDecodeCount(MorseCharacter ch);

	Integer getWordLengthSuccessPercentage(Integer wordLength);

	void recordWordLengthPerformance(LocalDateTime now, Integer percentage);

	Integer getMorseCharacterSuccessPercentage(MorseCharacter ch);

	void recordMorseCharacterPerformance(LocalDateTime now, Integer percentage);
}
