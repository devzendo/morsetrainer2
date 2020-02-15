package org.devzendo.morsetrainer2.qso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.MorseWord;
import org.devzendo.morsetrainer2.symbol.MorseWordResourceLoader;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;

public class CallsignGenerator {
	private final List<PartyMorseCharacter> out = new ArrayList<>();
	private final List<MorseWord> prefixes;

	public CallsignGenerator(final Integer groupSize) {
		prefixes = MorseWordResourceLoader.wordsFromResource("prefixes.txt");
		for (int i = 0; i < groupSize; i++) {
			final List<MorseCharacter> call = generateCallsign();
			call.forEach(mc -> {
				out.add(new PartyMorseCharacter(0, mc));
			});

			if (i != (groupSize - 1)) {
				out.add(new PartyMorseCharacter(0, MorseCharacter.SPC));
			}
		}
	}

	public Iterator<PartyMorseCharacter> iterator() {
		return out.iterator();
	}

	protected List<MorseCharacter> generateCallsign() {
		final List<MorseCharacter> out = new ArrayList<>();

		return out;
	}
}
