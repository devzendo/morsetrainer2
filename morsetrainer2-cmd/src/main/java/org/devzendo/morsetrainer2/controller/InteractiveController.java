package org.devzendo.morsetrainer2.controller;

import static org.devzendo.morsetrainer2.cmd.AnsiHelper.print;
import static org.devzendo.morsetrainer2.cmd.AnsiHelper.println;
import static org.devzendo.morsetrainer2.cmd.AnsiHelper.printlnraw;
import static org.devzendo.morsetrainer2.cmd.AnsiHelper.printraw;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.devzendo.commoncode.concurrency.ThreadUtils;
import org.devzendo.morsetrainer2.editmatcher.Edit;
import org.devzendo.morsetrainer2.editmatcher.EditMatcher;
import org.devzendo.morsetrainer2.iterator.PartyMorseCharacterIterator;
import org.devzendo.morsetrainer2.iterator.WordIterator;
import org.devzendo.morsetrainer2.player.Player;
import org.devzendo.morsetrainer2.stats.StatsStore;
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
	private final StatsStore statsStore;
	private final ConsoleReader consoleReader;

	public InteractiveController(final PartyMorseCharacterIterator it, final Player player, final StatsStore statsStore) {
		this.it = it;
		this.player = player;
		this.statsStore = statsStore;
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
		final Set<Integer> wordLengthsSent = new HashSet<>();
		final Set<MorseCharacter> morseCharactersDecodedSuccessfully = new HashSet<>();
		while (wit.hasNext()) {
			word = wit.next();
			wordMorseCharacters = Arrays.stream(word).map(pmc -> pmc.getRight()).toArray(MorseCharacter::allocate);
			wordLengthsSent.add(word.length);

			String entered = "";
			do {
				player.play(word);

				entered = getInput();
				if (!entered.isEmpty()) { // enter nothing to play word again

					final MorseCharacter[] enteredMorseCharacters = TextToMorseCharacterParser.parse(entered);
					statsStore.incrementWordLengthSentCount(word.length);
					if (Arrays.equals(wordMorseCharacters, enteredMorseCharacters)) {
						tick();
						statsStore.incrementWordLengthSuccessCount(word.length);
					} else {
						cross();
						// Levenshtein distance, count correct letters.
						// Increment occurrences of all chars in wordMorseCharacters
						for (final MorseCharacter mc : wordMorseCharacters) {
							statsStore.incrementSentCount(mc);
						}
						// Increment success count of all Match chars from the edits...
						printraw("compare: ");
						for (final Edit<MorseCharacter> edit : new EditMatcher<MorseCharacter>(wordMorseCharacters, enteredMorseCharacters).edits()) {
							if (edit.getType() == Edit.Type.Match) {
								print("@|green " + edit.getCh().toString() + "|@");
								statsStore.incrementSuccessfulDecodeCount(edit.getCh());
								morseCharactersDecodedSuccessfully.add(edit.getCh());
							} else {
								print("@|red " + edit.getCh().toString() + "|@");
							}
						}
						printraw(" ");
					}
					printraw("sent: ");
					for (final PartyMorseCharacter ch : word) {
						printraw(ch.getRight().toString());
					}
					printlnraw("");
					ThreadUtils.waitNoInterruption(250);
				}
			} while (entered.isEmpty());
		}

		// Record this session's performance
		recordSessionPerformance(wordLengthsSent, morseCharactersDecodedSuccessfully);
	}

	private void recordSessionPerformance(final Set<Integer> wordLengthsSent, final Set<MorseCharacter> morseCharactersDecodedSuccessfully) {
		final LocalDateTime now = LocalDateTime.now();

		for (final Integer wordLength: wordLengthsSent) {
			final Integer percentage = statsStore.getWordLengthSuccessPercentage(wordLength);
			statsStore.recordWordLengthPerformance(now, percentage);
		}

		for (final MorseCharacter ch: morseCharactersDecodedSuccessfully) {
			final Integer percentage = statsStore.getMorseCharacterSuccessPercentage(ch);
			statsStore.recordMorseCharacterPerformance(now, percentage);
		}
	}

	private void cross() {
		print("@|bold,red ✘ |@");
	}

	private void tick() {
		print("@|bold,green ✓ |@");
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
