package org.devzendo.morsetrainer2.stats;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;

public class MorseCharacterStat {
	private final MorseCharacter mc;
	private final int numberSent;
	private final int numberDecodedCorrectly;
	private final double accuracyPercentage;

	public MorseCharacterStat(final MorseCharacter mc, final int numberSent, final int numberDecodedCorrectly, final double accuracyPercentage) {
		this.mc = mc;
		this.numberSent = numberSent;
		this.numberDecodedCorrectly = numberDecodedCorrectly;
		this.accuracyPercentage = accuracyPercentage;
	}

	public MorseCharacter getMorseCharacter() {
		return mc;
	}

	public int getNumberSent() {
		return numberSent;
	}

	public int getNumberDecodedCorrectly() {
		return numberDecodedCorrectly;
	}

	public double getAccuracyPercentage() {
		return accuracyPercentage;
	}
}
