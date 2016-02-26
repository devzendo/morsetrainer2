package org.devzendo.morsetrainer2.controller;

import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.devzendo.morsetrainer2.iterator.PartyMorseCharacterIterator;
import org.devzendo.morsetrainer2.iterator.WordIterator;
import org.devzendo.morsetrainer2.player.Player;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;
import org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser;

public class InteractiveController implements Controller {

	private final PartyMorseCharacterIterator it;
	private final Player player;
	private final Scanner scanner;

	public InteractiveController(final PartyMorseCharacterIterator it, final Player player) {
		this.it = it;
		this.player = player;
		scanner = new Scanner(System.in);
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		final WordIterator wit = new WordIterator(it);

		PartyMorseCharacter[] word = null;
		while (wit.hasNext()) {
			word = wit.next();

			String entered = "";
			do {
				player.play(word);

				entered = getInput();
				if (!entered.isEmpty()) { // enter nothing to play word again
					final MorseCharacter[] enteredMorseCharacters = TextToMorseCharacterParser.parse(entered);
					incrementNumberSentAtLength(word.length);
					if (wordEqual(word, enteredMorseCharacters)) {
						tick();
						incrementSuccessForLength(word.length);
						// TODO Levenshtein distance, count correct letters.
					} else {
						cross();
					}
					for (final PartyMorseCharacter ch : word) {
						System.err.print(ch.getRight().toString());
					}
					System.err.println();
				}
			} while (entered.isEmpty());
		}
	}

	private void cross() {
		red();
		System.err.print("✘ ");
		normal();
	}

	private void tick() {
		green();
		System.err.print("✓ ");
		normal();
	}

	private void normal() {
		// TODO Auto-generated method stub

	}

	private void red() {
		// TODO Auto-generated method stub

	}

	private void green() {
		// TODO Auto-generated method stub

	}

	private boolean wordEqual(final PartyMorseCharacter[] word, final MorseCharacter[] enteredMorseCharacters) {
		final MorseCharacter[] wordChars = new MorseCharacter[word.length];
		for (int i = 0; i < word.length; i++) {
			wordChars[i] = word[i].getRight();
		}
		return Arrays.equals(wordChars, enteredMorseCharacters);
	}

	private void incrementSuccessForLength(final int length) {
		// TODO Auto-generated method stub

	}

	private void incrementNumberSentAtLength(final int length) {
		// TODO Auto-generated method stub

	}

	private String getInput() {
		return StringUtils.defaultString(scanner.next(), "").trim();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
