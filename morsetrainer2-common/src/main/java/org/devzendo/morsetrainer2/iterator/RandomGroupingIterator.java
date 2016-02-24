package org.devzendo.morsetrainer2.iterator;

import java.util.LinkedList;
import java.util.Optional;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;

public class RandomGroupingIterator implements PartyMorseCharacterIterator {
	private final Optional<Integer> length;
	private final MorseCharacter[] sourceSetArray;
	private int groupNumber;
	private LinkedList<PartyMorseCharacter> group;

	public RandomGroupingIterator(final Optional<Integer> length, final MorseCharacter[] sourceSetArray) {
		if (sourceSetArray == null || sourceSetArray.length == 0) {
			throw new IllegalArgumentException("Source set array cannot be null or empty");
		}
		this.length = length;
		this.sourceSetArray = sourceSetArray;
		this.groupNumber = 0;
		this.group = generate(generateGroupSize());
	}
	
	LinkedList<PartyMorseCharacter> generate(final int size) {
		final LinkedList<PartyMorseCharacter> out = new LinkedList<>();
		for (int i = 0; i < size; i++) {
			out.add(new PartyMorseCharacter(0, sourceSetArray[(int)(Math.random() * sourceSetArray.length)]));
		}
		return out;
	}

	int generateGroupSize() {
		return length.isPresent() ? length.get() : ((int)(Math.random() * 9) + 1); 
	}

	@Override
	public boolean hasNext() {
		if (group.isEmpty()) {
			if (groupNumber == 24) {
				return false;
			}
		}
		return true;
	}

	@Override
	public PartyMorseCharacter next() {
		if (group.isEmpty()) {
			groupNumber++;
			group = generate(generateGroupSize());
			return new PartyMorseCharacter(0, MorseCharacter.SPC);
		} else {
			return group.pop();
		}
	}
}
