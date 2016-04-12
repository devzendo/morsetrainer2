package org.devzendo.morsetrainer2.controller;

import java.io.PrintStream;
import java.util.Optional;

import org.devzendo.morsetrainer2.iterator.PartyMorseCharacterIterator;
import org.devzendo.morsetrainer2.player.Player;
import org.devzendo.morsetrainer2.stats.StatsStore;

public class ControllerFactory {

	public static Controller createController(final boolean interactive, final PartyMorseCharacterIterator it,
			final Player player, final StatsStore statsStore, final Optional<PrintStream> contentsPrintStream) {
		return interactive ? new InteractiveController(it, player, statsStore) : new NonInteractiveController(it, player, contentsPrintStream);
	}
}
