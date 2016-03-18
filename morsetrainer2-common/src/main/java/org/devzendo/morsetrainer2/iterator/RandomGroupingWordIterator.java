package org.devzendo.morsetrainer2.iterator;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.MorseWord;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomGroupingWordIterator implements PartyMorseCharacterIterator {
	private static Logger LOGGER = LoggerFactory.getLogger(RandomGroupingWordIterator.class);

	private final Optional<Integer> length;
	private int groupNumber;
	private LinkedList<PartyMorseCharacter> group;
	private final Map<Integer, ArrayList<MorseWord>> lengthMap;
	private final Integer[] lengths;

	public RandomGroupingWordIterator(final Optional<Integer> length, final Set<MorseWord> sourceWordList) {
		if (sourceWordList == null || sourceWordList.isEmpty()) {
			throw new IllegalArgumentException("Source word list cannot be null or empty");
		}
		this.length = length;
		lengthMap = initialiseLengthMap(sourceWordList);
		lengths = lengthMap.keySet().toArray(new Integer[0]);

		if (LOGGER.isDebugEnabled()) {
			final List<String> lenStrings = Arrays.asList(lengths).stream().map(i -> i + "").collect(Collectors.toList());
			LOGGER.debug("Available lengths: " + String.join(" ",  lenStrings));
		}

		length.ifPresent(i -> {
			if (!lengthMap.containsKey(i)) throw new IllegalArgumentException("Source word list has no words of length " + i);
		});

		reset();
	}

	public void reset() {
		this.groupNumber = 0;
		this.group = generate(generateGroupSize());
	}

	private Map<Integer, ArrayList<MorseWord>> initialiseLengthMap(final Set<MorseWord> sourceWordList) {
		final Map<Integer, ArrayList<MorseWord>> lengthMap = new HashMap<>();
		sourceWordList.forEach(w -> {
			if (w.size() <= 0 || w.size() >= 10) {
				final String word = asList(w).stream().map(mcc -> mcc.toString()).collect(joining());
				throw new IllegalArgumentException("Word '" + word + "' is of an incorrect length");
			}
			if (!lengthMap.containsKey(w.size())) {
				lengthMap.put(w.size(), new ArrayList<MorseWord>());
			}
			final ArrayList<MorseWord> lenList = lengthMap.get(w.size());
			lenList.add(w);
		});
		return lengthMap;
	}

	LinkedList<PartyMorseCharacter> generate(final int size) {
		final ArrayList<MorseWord> wordArrayList = lengthMap.get(size);
		final MorseWord word = wordArrayList.get((int)(Math.random() * wordArrayList.size()));
		final LinkedList<PartyMorseCharacter> out = new LinkedList<>();
		for (int i = 0; i < size; i++) {
			out.add(new PartyMorseCharacter(0, word.get(i)));
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
