package org.devzendo.morsetrainer2.stats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;

public interface StatsStore {

	void incrementWordLengthSentCount(int length);

	void incrementWordLengthSuccessCount(int length);

	WordLengthStat getWordLengthStatistics(int length);

	Double getWordLengthSuccessPercentage(int wordLength);


	void recordWordLengthPerformance(LocalDateTime when, int wordLength, Double percentage);

	List<WordLengthPerformance> getWordLengthPerformance(int wordLength, LocalDateTime from, LocalDateTime to);


	void incrementSentCount(MorseCharacter mc);

	void incrementSuccessfulDecodeCount(MorseCharacter ch);

	Double getMorseCharacterSuccessPercentage(MorseCharacter ch);


	void recordMorseCharacterPerformance(LocalDateTime when, MorseCharacter ch, Double percentage);

	List<MorseCharacterPerformance> getMorseCharacterPerformance(MorseCharacter ch, LocalDateTime from,
			LocalDateTime to);

	List<MorseCharacterStat> getStatisticsSortedByAccuracy(Set<MorseCharacter> morseCharactersSent);


	void close();
}
