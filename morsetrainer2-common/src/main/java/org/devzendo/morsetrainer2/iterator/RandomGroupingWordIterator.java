package org.devzendo.morsetrainer2.iterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;
import org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser;

public class RandomGroupingWordIterator implements PartyMorseCharacterIterator {
	private final Optional<Integer> length;
	private int groupNumber;
	private LinkedList<PartyMorseCharacter> group;
	private final Map<Integer, ArrayList<MorseCharacter[]>> lengthMap;
	private final Integer[] lengths;

	public RandomGroupingWordIterator(final Optional<Integer> length, final List<String> sourceWordList) {
		if (sourceWordList == null || sourceWordList.isEmpty()) {
			throw new IllegalArgumentException("Source word list cannot be null or empty");
		}
		this.length = length;
		lengthMap = initialiseLengthMap(sourceWordList);
		lengths = lengthMap.keySet().toArray(new Integer[0]);

		length.ifPresent(i -> {
			if (!lengthMap.containsKey(i)) throw new IllegalArgumentException("Source word list has no words of length " + i);
		});

		reset();
	}

	public void reset() {
		this.groupNumber = 0;
		this.group = generate(generateGroupSize());
	}

	private Map<Integer, ArrayList<MorseCharacter[]>> initialiseLengthMap(final List<String> sourceWordList) {
		final Map<Integer, ArrayList<MorseCharacter[]>> lengthMap = new HashMap<>();
		sourceWordList.forEach(w -> {
			if (w.length() <= 0 || w.length() >= 10) {
				throw new IllegalArgumentException("Word '" + w + "' is of an incorrect length");
			}
			final MorseCharacter[] wordMorseChars = TextToMorseCharacterParser.parse(w);
			final int len = wordMorseChars.length;
			if (!lengthMap.containsKey(len)) {
				lengthMap.put(len, new ArrayList<MorseCharacter[]>());
			}
			final ArrayList<MorseCharacter[]> lenList = lengthMap.get(len);
			lenList.add(wordMorseChars);
		});
		return lengthMap;
	}

	LinkedList<PartyMorseCharacter> generate(final int size) {
		final ArrayList<MorseCharacter[]> wordArrayList = lengthMap.get(size);
		final MorseCharacter[] word = wordArrayList.get((int)(Math.random() * wordArrayList.size()));
		final LinkedList<PartyMorseCharacter> out = new LinkedList<>();
		for (int i = 0; i < size; i++) {
			out.add(new PartyMorseCharacter(0, word[i]));
		}
		return out;
	}

	int generateGroupSize() {
		return length.isPresent() ? length.get() : randomLengthThatWeHaveWordsFor();
	}

	private int randomLengthThatWeHaveWordsFor() {
		return lengths[(int)(Math.random() * lengths.length)];
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
