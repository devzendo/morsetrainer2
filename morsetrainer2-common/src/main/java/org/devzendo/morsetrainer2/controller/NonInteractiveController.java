package org.devzendo.morsetrainer2.controller;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Optional;

import org.devzendo.morsetrainer2.iterator.WordIterator;
import org.devzendo.morsetrainer2.player.Player;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;

public class NonInteractiveController implements Controller {

	private static final PartyMorseCharacter SPACE = new PartyMorseCharacter(0, MorseCharacter.SPC);
	private final Iterator<PartyMorseCharacter> it;
	private final Player player;
	private final Optional<PrintStream> contentsPrintStream;
	private int xCursor;
	private boolean needNewline;

	public NonInteractiveController(final Iterator<PartyMorseCharacter> it, final Player player,
			final Optional<PrintStream> contentsPrintStream) {
		this.it = it;
		this.player = player;
		this.contentsPrintStream = contentsPrintStream;
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
	private void print(final PartyMorseCharacter... chars) {
		for (final PartyMorseCharacter pmc : chars) {
			final MorseCharacter mc = pmc.getRight();
			if (mc == MorseCharacter.SPC && xCursor >= 70) {
				xCursor = 0;
				needNewline = false;
				outputNewline();
				continue;
			}
			xCursor+= mc.toString().length();
			needNewline = true;
			outputString(mc.toString());
		}
	}

	private void outputString(final String string) {
		System.err.print(string);
		System.err.flush();
		contentsPrintStream.ifPresent(ps -> ps.print(string));
	}

	private void outputNewline() {
		System.err.println();
		System.err.flush();
		contentsPrintStream.ifPresent(ps -> ps.println());
	}

	@Override
	public void finish() {
		if (needNewline) {
			outputNewline();
		}
		System.err.flush();
	}
}
