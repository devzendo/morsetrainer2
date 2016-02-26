package org.devzendo.morsetrainer2.controller;

import org.devzendo.morsetrainer2.iterator.PartyMorseCharacterIterator;
import org.devzendo.morsetrainer2.iterator.WordIterator;
import org.devzendo.morsetrainer2.player.Player;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;

public class NonInteractiveController implements Controller {

	private static final PartyMorseCharacter SPACE = new PartyMorseCharacter(0, MorseCharacter.SPC);
	private final PartyMorseCharacterIterator it;
	private final Player player;
	private int xCursor;
	private boolean needNewline;

	public NonInteractiveController(final PartyMorseCharacterIterator it, final Player player) {
		this.it = it;
		this.player = player;
		this.xCursor = 0;
		this.needNewline = false;
	}

	@Override
	public void prepare() {
		// do nothing
	}

	@Override
	public void start() {
		final WordIterator wit = new WordIterator(it);
		while (wit.hasNext()) {
			final PartyMorseCharacter[] word = wit.next();
			player.play(word);
			print(word);

			if (wit.hasNext()) {
				player.play(SPACE);
				print(SPACE);
			}
		}
	}

	// complex bit of code that isn't tested, ew....
	private void print(final PartyMorseCharacter ... chars) {
		for (final PartyMorseCharacter pmc : chars) {
			final MorseCharacter mc = pmc.getRight();
			if (mc == MorseCharacter.SPC && xCursor >= 70) {
				xCursor = 0;
				needNewline = false;
				System.err.println();
				continue;
			}
			xCursor++;
			needNewline = false;
			System.err.print(mc.toString());
		}
	}

	@Override
	public void finish() {
		if (needNewline) {
			System.err.println();
		}
	}
}
