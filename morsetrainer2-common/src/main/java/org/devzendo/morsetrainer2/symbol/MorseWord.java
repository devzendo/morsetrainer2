package org.devzendo.morsetrainer2.symbol;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;

public class MorseWord implements Comparable<MorseWord> {
	private final MorseCharacter[] mcs;

	public MorseCharacter[] getArray() {
		return mcs;
	}

	public MorseWord(final MorseCharacter ... mcs) {
		this.mcs = mcs;
	}

	public MorseWord(final List<MorseCharacter> list) {
		this(list.toArray(new MorseCharacter[0]));
	}

	public int size() {
		return mcs.length;
	}

	@Override
	public String toString() {
		return asList(mcs).stream().map(mc -> mc.toString()).collect(joining());
	}

	public MorseCharacter get(final int i) {
		return mcs[i];
	}

	@Override
	public boolean equals(final Object other) {
        if (!(other instanceof MorseWord))
            return false;
        if (other == this)
            return true;
        final MorseWord mwother = (MorseWord)other;
        return Arrays.equals(mwother.mcs, this.mcs);
	}

	@Override
	public int hashCode() {
		return mcs.hashCode();
	}

	@Override
	public int compareTo(final MorseWord other) {
		return toString().compareTo(other.toString());
	}
}
