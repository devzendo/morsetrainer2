package org.devzendo.morsetrainer2.qso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;

public class CallsignGenerator {
	private final Integer groupSize;
	private final List<PartyMorseCharacter> out = new ArrayList<>();

	public CallsignGenerator(final Integer groupSize) {
		this.groupSize = groupSize;
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
		return Arrays.asList(MorseCharacter.M, MorseCharacter.D0, MorseCharacter.C, MorseCharacter.U, MorseCharacter.V);
	}
}
