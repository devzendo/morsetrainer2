package org.devzendo.morsetrainer2.iterator;

import static java.util.Collections.emptySet;
import static org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser.parseMultipleToList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.devzendo.morsetrainer2.logging.LoggingUnittest;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.MorseWord;
import org.devzendo.morsetrainer2.symbol.MorseWordResourceLoader;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestRandomGroupingWordIterator {

	@BeforeClass
	public static void setupLogging() {
		LoggingUnittest.initialise();
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void sourceWordListCannotBeNull() throws Exception {
		constructWithBadSourceWordList(null);
	}

	@Test
	public void sourceWordListCannotBeEmpty() throws Exception {
		constructWithBadSourceWordList(emptySet());
	}

	@Test
	public void sourceWordListHasNothingOfSpecifiedLength() throws Exception {
		thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Source word list has no words of length 3");
        new RandomGroupingWordIterator(0, Optional.of(3), toSet(parseMultipleToList("A", "ZZZZ", "FF")));
	}

	@Test
	public void zeroLengthWordNotAllowed() throws Exception {
		thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Word '' is of an incorrect length");
        new RandomGroupingWordIterator(0, Optional.of(3), toSet(parseMultipleToList("")));
	}

	@Test
	public void tenLengthWordNotAllowed() throws Exception {
		thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Word 'ABCDEFGHIJ' is of an incorrect length");
        new RandomGroupingWordIterator(0, Optional.of(3), toSet(parseMultipleToList("ABCDEFGHIJ")));
	}

	private void constructWithBadSourceWordList(final Set<MorseWord> sourceWordList) {
		thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Source word list cannot be null or empty");
        new RandomGroupingWordIterator(0, Optional.of(3), sourceWordList);
	}

	@Test
	public void getSomeFixedLengthWords() throws Exception {
        final RandomGroupingWordIterator it = new RandomGroupingWordIterator(25, Optional.of(3), toSet(parseMultipleToList("A", "BB", "CCC", "xxx", "yyy", "zzz", "DDDD", "EEEEE")));
        for (int i = 0; i < 25; i++ ) {
        	final Set<PartyMorseCharacter> all = new HashSet<>();
        	for (int j = 0; j < 3; j++ ) {
        		assertThat(it.hasNext(), equalTo(true));
        		all.add(it.next());
        	}
        	assertThat(all, hasSize(1));
        	final String mcs = all.toArray(new PartyMorseCharacter[0])[0].getRight().toString();
        	assertThat(mcs, Matchers.oneOf("C", "X", "Y", "Z"));
        	if (i == 24) {
	    		assertThat(it.hasNext(), equalTo(false));
        	} else {
	    		assertThat(it.hasNext(), equalTo(true));
	    		assertThat(it.next(), equalTo(new PartyMorseCharacter(0, MorseCharacter.SPC)));
        	}
        }
	}

	@Test
	public void generateGivenNumberOfWordsOfGivenSize() throws Exception {
		final RandomGroupingWordIterator it = new RandomGroupingWordIterator(4, Optional.of(3), toSet(parseMultipleToList("AAA", "BBB", "CCC")));
		for (int i = 0; i < 4; i++) {
			assertThat(it.hasNext(), equalTo(true));
			final StringBuilder sb = new StringBuilder();
			for (int j = 0; j < 3; j++) {
				assertThat(it.hasNext(), equalTo(true));
				sb.append(it.next().getRight().toString());
			}
			final String str = sb.toString();
			assertThat(str, matchesPattern("^[ABC]{3}$"));

			if (i == 3) {
				assertThat(it.hasNext(), equalTo(false));
			} else {
				assertThat(it.hasNext(), equalTo(true));
				assertThat(it.next().getRight(), equalTo(MorseCharacter.SPC));
			}
		}
	}

	@Test
	public void getSomeRandomLengthWords() throws Exception {
        final RandomGroupingWordIterator it = new RandomGroupingWordIterator(25, Optional.empty(), toSet(parseMultipleToList("1", "22", "333", "4444", "55555", "666666", "7777777", "88888888", "999999999")));
    	final Set<Integer> seen = new HashSet<>();
    	boolean seenSpaces = false;
        while (it.hasNext()) {
        	final MorseCharacter rmc = it.next().getRight();
    		if (rmc == MorseCharacter.SPC) {
    			seenSpaces = true;
    		} else {
    			final Integer intChar = Integer.parseInt(rmc.toString());
    			seen.add(intChar);
    		}
        }
        assertThat(seenSpaces, equalTo(true));
        // bit flaky, this...
        assertThat(seen.size(), greaterThan(3));
	}

	@Test
	public void seenAllRandomLengthWords() throws Exception {
        final RandomGroupingWordIterator it = new RandomGroupingWordIterator(25, Optional.empty(), toSet(parseMultipleToList("1", "22", "333", "4444", "55555", "666666", "7777777", "88888888", "999999999")));
    	final Set<Integer> seen = new HashSet<>();
    	for (int i = 0; i < 100; i++ ) {
    		it.reset();
    		while (it.hasNext()) {
    			final MorseCharacter rmc = it.next().getRight();
    			if (rmc != MorseCharacter.SPC) {
    				final Integer intChar = Integer.parseInt(rmc.toString());
    				seen.add(intChar);
    			}
    		}
    	}
        // bit flaky, this...
        assertThat(seen.size(), equalTo(9));
	}

	@Test
	public void codesAllOk() throws Exception {
		new RandomGroupingWordIterator(25, Optional.empty(), toSet(MorseWordResourceLoader.wordsFromResource("codes.txt")));
	}

	private Set<MorseWord> toSet(final List<MorseWord> words) {
		return new HashSet<MorseWord>(words);
	}
}
