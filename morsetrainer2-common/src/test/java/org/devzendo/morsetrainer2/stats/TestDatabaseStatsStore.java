package org.devzendo.morsetrainer2.stats;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;

import org.devzendo.morsetrainer2.logging.LoggingUnittest;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.h2.engine.ExistenceChecker;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDatabaseStatsStore {

	private static final int WORD_LENGTH = 5;
	private static final double TOLERANCE = 0.1;
	private static final Logger LOGGER = LoggerFactory.getLogger(TestDatabaseStatsStore.class);
	private static final MorseCharacter A = MorseCharacter.A;

	@Rule
	public final TemporaryFolder tempDir = new TemporaryFolder();
	private File root;
	private StatsStore store;

	@BeforeClass
	public static void setupLogging() {
		LoggingUnittest.initialise();
	}

	@Before
	public void setupTempfile() throws IOException {
		tempDir.create();
		root = tempDir.getRoot();
		LOGGER.info("db is at " + root.getAbsolutePath());
		store = new DatabaseStatsStore(root);
	}

	@After
	public void closeDb() {
		store.close();
	}

	@Test
	public void dbFileExists() {
		assertThat(ExistenceChecker.exists(new File(root, "morsetrainer").getAbsolutePath()), equalTo(true));
	}

	@Test
	public void statsAreInitiallyEmpty() throws Exception {
		final MorseCharacterStat stat = statsFor(A);
		assertThat(stat.getNumberSent(), equalTo(0));
		assertThat(stat.getAccuracyPercentage(), equalTo(0.0));
		assertThat(stat.getMorseCharacter(), equalTo(A));
		assertThat(stat.getNumberDecodedCorrectly(), equalTo(0));
	}

	@Test
	public void canIncrementSentCount() throws Exception {
		store.incrementSentCount(A);
		final MorseCharacterStat stat = statsFor(A);
		assertThat(stat.getNumberSent(), equalTo(1));

		store.incrementSentCount(A);
		assertThat(statsFor(A).getNumberSent(), equalTo(2));
	}

	@Test
	public void canIncrementSuccessCount() throws Exception {
		store.incrementSuccessfulDecodeCount(A);
		final MorseCharacterStat stat = statsFor(A);
		assertThat(stat.getNumberDecodedCorrectly(), equalTo(1));

		store.incrementSuccessfulDecodeCount(A);
		assertThat(statsFor(A).getNumberDecodedCorrectly(), equalTo(2));
	}

	@Test
	public void successPercentageCalculatedCorrectly() throws Exception {
		assertThat(perc(A), is(closeTo(0.0, TOLERANCE))); // succ 0 / sent 0

		store.incrementSentCount(A);
		store.incrementSuccessfulDecodeCount(A);
		assertThat(perc(A), is(closeTo(100.0, TOLERANCE))); // succ 1 / sent 1

		store.incrementSentCount(A);
		assertThat(perc(A), is(closeTo(50.0, TOLERANCE))); // succ 1 / sent 2

		store.incrementSentCount(A);
		assertThat(perc(A), is(closeTo(33.3, TOLERANCE))); // succ 1 / sent 3

		store.incrementSuccessfulDecodeCount(A);
		assertThat(perc(A), is(closeTo(66.6, TOLERANCE))); // succ 2 / sent 3

		assertThat(store.getMorseCharacterSuccessPercentage(A), is(closeTo(66.6, TOLERANCE))); // the 'get just the percentage' API

		store.incrementSuccessfulDecodeCount(A);
		assertThat(perc(A), is(closeTo(100.0, TOLERANCE))); // succ 3 / sent 3
	}

	@Test
	public void wordLengthSentCountInitiallyZero() throws Exception {
		assertThat(perc(WORD_LENGTH), is(closeTo(0.00, TOLERANCE))); // succ 0 / sent 0
	}

	@Test
	public void wordLengthStatsInitiallyZero() throws Exception {
		final WordLengthStat stat = statsFor(WORD_LENGTH);
		assertThat(stat.getLength(), equalTo(WORD_LENGTH));
		assertThat(stat.getNumberDecodedCorrectly(), equalTo(0));
		assertThat(stat.getNumberSent(), equalTo(0));
	}

	@Test
	public void canIncrementWordLengthSentCount() throws Exception {
		store.incrementWordLengthSentCount(WORD_LENGTH); // succ 0 / sent 1
		assertThat(statsFor(WORD_LENGTH).getNumberSent(), equalTo(1));

		store.incrementWordLengthSentCount(WORD_LENGTH); // succ 0 / sent 2
		assertThat(statsFor(WORD_LENGTH).getNumberSent(), equalTo(2));
	}

	@Test
	public void canIncrementWordLengthSuccessCount() throws Exception {
		store.incrementWordLengthSuccessCount(WORD_LENGTH); // succ 1 / sent 0
		assertThat(statsFor(WORD_LENGTH).getNumberDecodedCorrectly(), equalTo(1));

		store.incrementWordLengthSuccessCount(WORD_LENGTH); // succ 2 / sent 0
		assertThat(statsFor(WORD_LENGTH).getNumberDecodedCorrectly(), equalTo(2));
	}

	@Test
	public void wordLengthSuccessPercentageCalculatedCorrectly() throws Exception {
		assertThat(perc(WORD_LENGTH), closeTo(0.0, TOLERANCE)); // succ 0 / sent 0

		store.incrementWordLengthSentCount(WORD_LENGTH);
		store.incrementWordLengthSuccessCount(WORD_LENGTH);
		assertThat(perc(WORD_LENGTH), closeTo(100.0, TOLERANCE)); // succ 1 / sent 1

		store.incrementWordLengthSentCount(WORD_LENGTH);
		assertThat(perc(WORD_LENGTH), closeTo(50.0, TOLERANCE)); // succ 1 / sent 2

		store.incrementWordLengthSentCount(WORD_LENGTH);
		assertThat(perc(WORD_LENGTH), closeTo(33.3, TOLERANCE)); // succ 1 / sent 3

		store.incrementWordLengthSuccessCount(WORD_LENGTH);
		assertThat(perc(WORD_LENGTH), closeTo(66.6, TOLERANCE)); // succ 2 / sent 3

		store.incrementWordLengthSuccessCount(WORD_LENGTH);
		assertThat(perc(WORD_LENGTH), closeTo(100.0, TOLERANCE)); // succ 3 / sent 3
	}

	private double perc(final int length) {
		return store.getWordLengthSuccessPercentage(length);
	}

	private double perc(final MorseCharacter theChar) {
		return statsFor(theChar).getAccuracyPercentage();
	}

	private WordLengthStat statsFor(final int length) {
		return store.getWordLengthStatistics(length);
	}

	private MorseCharacterStat statsFor(final MorseCharacter theChar) {
		return store.getStatisticsSortedByAccuracy(new HashSet<MorseCharacter>(asList(theChar))).get(0);
	}

	@Test
	public void correctlySortedReport() {
		send(5, 5, MorseCharacter.A); // 100.0%
		send(5, 4, MorseCharacter.B); // 80.0%
		send(15, 5, MorseCharacter.C); // 33.33%
		send(8, 0, MorseCharacter.D); // 0.0%
		send(8, 1, MorseCharacter.E); // 12.5%

		send(8, 3, MorseCharacter.Z); // won't be queried for

		final HashSet<MorseCharacter> set = new HashSet<>(asList(MorseCharacter.A, MorseCharacter.B, MorseCharacter.C, MorseCharacter.D, MorseCharacter.E));
		final List<MorseCharacterStat> stats = store.getStatisticsSortedByAccuracy(set);

		assertThat(stats, hasSize(5));

		assertPerformance(stats.get(0), MorseCharacter.D, 0.0);
		assertPerformance(stats.get(1), MorseCharacter.E, 12.5);
		assertPerformance(stats.get(2), MorseCharacter.C, 33.3);
		assertPerformance(stats.get(3), MorseCharacter.B, 80.0);
		assertPerformance(stats.get(4), MorseCharacter.A, 100.0);
	}

	private void assertPerformance(final MorseCharacterStat stat, final MorseCharacter isCh, final double isPerc) {
		assertThat(stat.getMorseCharacter(), equalTo(isCh));
		assertThat(stat.getAccuracyPercentage(), closeTo(isPerc, TOLERANCE));
	}

	@Test
	public void charPerformanceStatistics() throws Exception {
		charPerform(5, 0.2);
		charPerform(6, 0.1);
		charPerform(8, 0.2);
		charPerform(12, 1.9);
		charPerform(14, 4.1); // Existing entries can be updated
		charPerform(14, 4.5); // and again...
		charPerform(14, 4.0); // This is the final one.
		charPerform(15, 0.1); // A blip.
		charPerform(2, 0.1);  // Out of order storage shouldn't matter.
		charPerform(18, 8.9);
		charPerform(20, 15.0);
		charPerform(22, 40.0);
		charPerform(23, 50.0);
		charPerform(24, 75.0);
		charPerform(25, 80.0);
		charPerform(27, 99.2);

		final List<MorseCharacterPerformance> stats = store.getMorseCharacterPerformance(A, when(3), when(26)); // neither 3 nor 26 are stored events, but want to return that closed interval
		assertThat(stats, hasSize(12));

		assertPerformanceWhen(stats.get(0), 5, 0.2);
		assertPerformanceWhen(stats.get(1), 6, 0.1);
		assertPerformanceWhen(stats.get(2), 8, 0.2);
		assertPerformanceWhen(stats.get(3), 12, 1.9);
		assertPerformanceWhen(stats.get(4), 14, 4.0);
		assertPerformanceWhen(stats.get(5), 15, 0.1);
		assertPerformanceWhen(stats.get(6), 18, 8.9);
		assertPerformanceWhen(stats.get(7), 20, 15.0);
		assertPerformanceWhen(stats.get(8), 22, 40.0);
		assertPerformanceWhen(stats.get(9), 23, 50.0);
		assertPerformanceWhen(stats.get(10), 24, 75.0);
		assertPerformanceWhen(stats.get(11), 25, 80.0);
	}

	@Test
	public void lengthPerformanceStatistics() throws Exception {
		lengthPerform(5, 0.2);
		lengthPerform(6, 0.1);
		lengthPerform(8, 0.2);
		lengthPerform(12, 1.9);
		lengthPerform(14, 4.1); // Existing entries can be updated
		lengthPerform(14, 4.5); // and again...
		lengthPerform(14, 4.0); // This is the final one.
		lengthPerform(15, 0.1); // A blip.
		lengthPerform(2, 0.1);  // Out of order storage shouldn't matter.
		lengthPerform(18, 8.9);
		lengthPerform(20, 15.0);
		lengthPerform(22, 40.0);
		lengthPerform(23, 50.0);
		lengthPerform(24, 75.0);
		lengthPerform(25, 80.0);
		lengthPerform(27, 99.2);

		final List<WordLengthPerformance> stats = store.getWordLengthPerformance(WORD_LENGTH, when(3), when(26)); // neither 3 nor 26 are stored events, but want to return that closed interval
		assertThat(stats, hasSize(12));

		assertLengthPerformanceWhen(stats.get(0), 5, 0.2);
		assertLengthPerformanceWhen(stats.get(1), 6, 0.1);
		assertLengthPerformanceWhen(stats.get(2), 8, 0.2);
		assertLengthPerformanceWhen(stats.get(3), 12, 1.9);
		assertLengthPerformanceWhen(stats.get(4), 14, 4.0);
		assertLengthPerformanceWhen(stats.get(5), 15, 0.1);
		assertLengthPerformanceWhen(stats.get(6), 18, 8.9);
		assertLengthPerformanceWhen(stats.get(7), 20, 15.0);
		assertLengthPerformanceWhen(stats.get(8), 22, 40.0);
		assertLengthPerformanceWhen(stats.get(9), 23, 50.0);
		assertLengthPerformanceWhen(stats.get(10), 24, 75.0);
		assertLengthPerformanceWhen(stats.get(11), 25, 80.0);
	}

	private void assertPerformanceWhen(final MorseCharacterPerformance stat, final long secondsFromEpoch, final double performance) {
		assertThat(stat.getMorseCharacter(), equalTo(A)); // they all are
		assertThat(stat.getAccuracyPercentage(), closeTo(performance, TOLERANCE));
		assertThat(stat.getWhen(), equalTo(when(secondsFromEpoch)));
	}

	private void charPerform(final long secondsFromEpoch, final double performance) {
		final LocalDateTime when = when(secondsFromEpoch);
		store.recordMorseCharacterPerformance(when, A, performance);
	}

	private void lengthPerform(final long secondsFromEpoch, final double performance) {
		final LocalDateTime when = when(secondsFromEpoch);
		store.recordWordLengthPerformance(when, WORD_LENGTH, performance);
	}

	private void assertLengthPerformanceWhen(final WordLengthPerformance stat, final long secondsFromEpoch, final double performance) {
		assertThat(stat.getWordLength(), equalTo(WORD_LENGTH)); // they all are
		assertThat(stat.getAccuracyPercentage(), closeTo(performance, TOLERANCE));
		assertThat(stat.getWhen(), equalTo(when(secondsFromEpoch)));
	}

	private LocalDateTime when(final long secondsFromEpoch) {
		final LocalDateTime when = LocalDateTime.ofEpochSecond(secondsFromEpoch, 0, ZoneOffset.UTC);
		return when;
	}

	private void send(final int sentCount, final int successCount, final MorseCharacter ch) {
		for (int j = 0; j < sentCount; j++) {
			store.incrementSentCount(ch);
		}
		for (int j = 0; j < successCount; j++) {
			store.incrementSuccessfulDecodeCount(ch);
		}
	}

}
