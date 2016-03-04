package org.devzendo.morsetrainer2.controller;

import static org.devzendo.morsetrainer2.cmd.AnsiHelper.print;
import static org.devzendo.morsetrainer2.cmd.AnsiHelper.println;
import static org.devzendo.morsetrainer2.cmd.AnsiHelper.printlnraw;
import static org.devzendo.morsetrainer2.cmd.AnsiHelper.printraw;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.devzendo.commoncode.concurrency.ThreadUtils;
import org.devzendo.morsetrainer2.iterator.PartyMorseCharacterIterator;
import org.devzendo.morsetrainer2.iterator.WordIterator;
import org.devzendo.morsetrainer2.player.Player;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;
import org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser;
import org.fusesource.jansi.AnsiRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jline.console.ConsoleReader;

public class InteractiveController implements Controller {
	private static final Logger LOGGER = LoggerFactory.getLogger(InteractiveController.class);
	private static final String PROMPT = AnsiRenderer.render("@|yellow,bold ? |@");

	private final PartyMorseCharacterIterator it;
	private final Player player;
	private final ConsoleReader consoleReader;

	public InteractiveController(final PartyMorseCharacterIterator it, final Player player) {
		this.it = it;
		this.player = player;
        try {
			consoleReader = new ConsoleReader();
		} catch (final IOException e) {
			final String msg = "Couldn't start ConsoleReader: " + e.getMessage();
			LOGGER.error(msg);
			throw new IllegalStateException(msg);
		}
	}

	@Override
	public void prepare() {
		println("At the @|yellow,bold ?|@ prompt, enter the string of Morse you heard,");
		printlnraw("with <..> around any prosigns, then press ENTER.");
		printlnraw("Just press ENTER without entering anything to play the string again.");
		printlnraw("");
		try {
			consoleReader.readLine(AnsiRenderer.render("@|green FIRST, PRESS ENTER TO HEAR VVV THEN YOU'RE OFF! |@"));
		} catch (final IllegalArgumentException e) {
		} catch (final IOException e) {
		}
	}

	@Override
	public void start() {
		final WordIterator wit = new WordIterator(it);
		// TODO get number of groups, display current/max groups in prompt?

		PartyMorseCharacter[] word = null;
		MorseCharacter[] wordMorseCharacters = null;
		while (wit.hasNext()) {
			word = wit.next();
			wordMorseCharacters = (MorseCharacter[]) Arrays.stream(word).map(pmc -> pmc.getRight()).toArray(MorseCharacter::allocate);

			String entered = "";
			do {
				player.play(word);

				entered = getInput();
				if (!entered.isEmpty()) { // enter nothing to play word again

					final MorseCharacter[] enteredMorseCharacters = TextToMorseCharacterParser.parse(entered);
					incrementNumberSentAtLength(word.length);
					if (Arrays.equals(wordMorseCharacters, enteredMorseCharacters)) {
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

	private void incrementSuccessForLength(final int length) {
		// TODO Auto-generated method stub

	}

	private void incrementNumberSentAtLength(final int length) {
		// TODO Auto-generated method stub

	}

	private String getInput() {
		try {
			return StringUtils.defaultString(consoleReader.readLine(PROMPT), "").trim();
		} catch (final IOException e) {
			LOGGER.warn("ConsoleReader failed to read: " + e.getMessage());
			return "";
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
