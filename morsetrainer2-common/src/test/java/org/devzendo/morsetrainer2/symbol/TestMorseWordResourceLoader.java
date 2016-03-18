package org.devzendo.morsetrainer2.symbol;

import static org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser.parseMultipleToList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.devzendo.morsetrainer2.logging.LoggingUnittest;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMorseWordResourceLoader {

	private static Logger LOGGER = LoggerFactory.getLogger(TestMorseWordResourceLoader.class);

	@BeforeClass
	public static void setupLogging() {
		LoggingUnittest.initialise();
	}

	@Test
	public void resourceFileCanBeLoaded() {
		final List<MorseWord> words = MorseWordResourceLoader.wordsFromResource("testfile.txt");
		assertThat(words, equalTo(parseMultipleToList("THIS", "IS", "A", "TEST")));
	}

	@Test
	public void codesCanBeLoaded() throws Exception {
		final List<MorseWord> words = MorseWordResourceLoader.wordsFromResource("codes.txt");
		for (final MorseWord word : words) {
			LOGGER.debug("Word '{}'", word);
		}
		final List<MorseWord> firstExpectedWords = parseMultipleToList("AA", "AB", "ARRL", "ABT", "ADR", "AGN", "AM", "ANT", "ARND", "BCI"); // etc., etc.
		final List<MorseWord> firstReturnedWords = words.subList(0, firstExpectedWords.size());
		assertThat(firstReturnedWords, equalTo(firstExpectedWords));
		assertThat(words, Matchers.hasSize(206));
	}
}
