package org.devzendo.morsetrainer2.controller;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Optional;

import org.devzendo.morsetrainer2.player.Player;
import org.devzendo.morsetrainer2.stats.StatsStore;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;

public class ControllerFactory {

	public static Controller createController(final boolean interactive, final Iterator<PartyMorseCharacter> it,
			final Player player, final StatsStore statsStore, final Optional<PrintStream> contentsPrintStream) {
		return interactive ? new InteractiveController(it, player, statsStore) : new NonInteractiveController(it, player, contentsPrintStream);
	}
}
