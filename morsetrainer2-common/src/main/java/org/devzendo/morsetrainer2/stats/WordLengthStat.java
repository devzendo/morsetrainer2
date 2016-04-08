package org.devzendo.morsetrainer2.stats;

public class WordLengthStat {
	private final int length;
	private final int numberSent;
	private final int numberDecodedCorrectly;
	private final double accuracyPercentage;

	public WordLengthStat(final int length, final int numberSent, final int numberDecodedCorrectly, final double accuracyPercentage) {
		this.length = length;
		this.numberSent = numberSent;
		this.numberDecodedCorrectly = numberDecodedCorrectly;
		this.accuracyPercentage = accuracyPercentage;
	}

	public int getLength() {
		return length;
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
