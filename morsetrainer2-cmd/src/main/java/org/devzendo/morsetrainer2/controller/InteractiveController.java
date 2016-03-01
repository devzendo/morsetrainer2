package org.devzendo.morsetrainer2.controller;

import static org.devzendo.morsetrainer2.cmd.AnsiHelper.flush;
import static org.devzendo.morsetrainer2.cmd.AnsiHelper.print;
import static org.devzendo.morsetrainer2.cmd.AnsiHelper.println;
import static org.devzendo.morsetrainer2.cmd.AnsiHelper.printlnraw;
import static org.devzendo.morsetrainer2.cmd.AnsiHelper.printraw;

import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.devzendo.commoncode.concurrency.ThreadUtils;
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
		println("At the @|yellow ?|@ prompt, enter the string of Morse you heard,");
		printlnraw("with <..> around any prosigns, then press ENTER.");
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
						printraw(ch.getRight().toString());
					}
					printlnraw("");
					ThreadUtils.waitNoInterruption(250);
				}
			} while (entered.isEmpty());
		}
	}

	private void cross() {
		print("@|bold,red ✘ |@");
	}

	private void tick() {
		print("@|bold,green ✓ |@");
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
		print("@|yellow ? |@");
		flush();
		return StringUtils.defaultString(scanner.next(), "").trim();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
