package org.devzendo.morsetrainer2.iterator;

import java.util.ArrayList;
import java.util.Iterator;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;

public class WordIterator implements Iterator<PartyMorseCharacter[]>{

	private final PartyMorseCharacterIterator it;
	private final ArrayList<PartyMorseCharacter> list = new ArrayList<PartyMorseCharacter>();

	public WordIterator(final PartyMorseCharacterIterator it) {
		this.it = it;
		findNextWord();
	}

	private void findNextWord() {
		list.clear();
		boolean done = false;
		boolean foundWordYet = false;
		while (!done && it.hasNext()) {
			final PartyMorseCharacter pmc = it.next();
			final MorseCharacter mc = pmc.getRight();
			if (mc == MorseCharacter.SPC) {
				if (!foundWordYet) {
					continue;
				} else {
					// it's the end!
					done = true;
				}
			} else {
				foundWordYet = true;
				list.add(pmc);
			}
		}
	}

	@Override
	public boolean hasNext() {
		return !list.isEmpty();
	}

	@Override
	public PartyMorseCharacter[] next() {
		final PartyMorseCharacter[] ret = list.toArray(new PartyMorseCharacter[0]);
		findNextWord();
		return ret;
	}
}
