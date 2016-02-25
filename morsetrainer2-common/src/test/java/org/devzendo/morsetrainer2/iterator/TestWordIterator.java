package org.devzendo.morsetrainer2.iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;
import org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser;
import org.junit.Test;

public class TestWordIterator {

	@Test
	public void testEmptiness() {
		final WordIterator wit = new WordIterator(new VerbatimIterator(""));
		assertThat(wit.hasNext(), equalTo(false));
	}

	@Test
	public void testSingleWord() {
		final WordIterator wit = new WordIterator(new VerbatimIterator("123"));
		assertThat(wit.hasNext(), equalTo(true));
		assertThat(wit.next(), equalTo(toPMCArray("123")));
		assertThat(wit.hasNext(), equalTo(false));
	}

	@Test
	public void spaceStartingWord() {
		final WordIterator wit = new WordIterator(new VerbatimIterator(" 123"));
		assertThat(wit.hasNext(), equalTo(true));
		assertThat(wit.next(), equalTo(toPMCArray("123")));
		assertThat(wit.hasNext(), equalTo(false));
	}

	@Test
	public void spaceEndingWord() {
		final WordIterator wit = new WordIterator(new VerbatimIterator("123 "));
		assertThat(wit.hasNext(), equalTo(true));
		assertThat(wit.next(), equalTo(toPMCArray("123")));
		assertThat(wit.hasNext(), equalTo(false));
	}

	@Test
	public void multipleSpaceStartingWord() {
		final WordIterator wit = new WordIterator(new VerbatimIterator("     123"));
		assertThat(wit.hasNext(), equalTo(true));
		assertThat(wit.next(), equalTo(toPMCArray("123")));
		assertThat(wit.hasNext(), equalTo(false));
	}

	@Test
	public void multipleSpaceEndingWord() {
		final WordIterator wit = new WordIterator(new VerbatimIterator("123   "));
		assertThat(wit.hasNext(), equalTo(true));
		assertThat(wit.next(), equalTo(toPMCArray("123")));
		assertThat(wit.hasNext(), equalTo(false));
	}

	@Test
	public void twoSpaceBetweenWords() {
		final WordIterator wit = new WordIterator(new VerbatimIterator("123  456"));
		assertThat(wit.hasNext(), equalTo(true));
		assertThat(wit.next(), equalTo(toPMCArray("123")));
		assertThat(wit.hasNext(), equalTo(true));
		assertThat(wit.next(), equalTo(toPMCArray("456")));
		assertThat(wit.hasNext(), equalTo(false));
	}

	@Test
	public void manySpacesBetweenWords() {
		final WordIterator wit = new WordIterator(new VerbatimIterator("123     456"));
		assertThat(wit.hasNext(), equalTo(true));
		assertThat(wit.next(), equalTo(toPMCArray("123")));
		assertThat(wit.hasNext(), equalTo(true));
		assertThat(wit.next(), equalTo(toPMCArray("456")));
		assertThat(wit.hasNext(), equalTo(false));
	}

	@Test
	public void testNormalCase() {
		final WordIterator wit = new WordIterator(new VerbatimIterator("123 456 789"));
		assertThat(wit.hasNext(), equalTo(true));
		assertThat(wit.next(), equalTo(toPMCArray("123")));
		assertThat(wit.hasNext(), equalTo(true));
		assertThat(wit.next(), equalTo(toPMCArray("456")));
		assertThat(wit.hasNext(), equalTo(true));
		assertThat(wit.next(), equalTo(toPMCArray("789")));
		assertThat(wit.hasNext(), equalTo(false));
	}

	private PartyMorseCharacter[] toPMCArray(final String string) {
		final List<MorseCharacter> list = TextToMorseCharacterParser.parseToList(string);
		final PartyMorseCharacter[] array = new PartyMorseCharacter[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = new PartyMorseCharacter(0, list.get(i));
		}
		return array;
	}
}
