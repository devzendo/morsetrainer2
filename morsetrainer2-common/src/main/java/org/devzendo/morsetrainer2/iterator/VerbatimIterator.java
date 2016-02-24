package org.devzendo.morsetrainer2.iterator;

import java.util.Iterator;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;
import org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser;

public class VerbatimIterator implements PartyMorseCharacterIterator {

	private final Iterator<MorseCharacter> iterator;

	public VerbatimIterator(final String playString) {
		iterator = TextToMorseCharacterParser.parseToList(playString).iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public PartyMorseCharacter next() {
		return new PartyMorseCharacter(0, iterator.next()); // party 0 for now
	}
}
