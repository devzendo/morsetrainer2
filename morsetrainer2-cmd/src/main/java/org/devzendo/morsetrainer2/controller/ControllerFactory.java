package org.devzendo.morsetrainer2.controller;

import org.devzendo.morsetrainer2.iterator.PartyMorseCharacterIterator;
import org.devzendo.morsetrainer2.player.Player;
import org.devzendo.morsetrainer2.stats.StatsStore;

public class ControllerFactory {

	public static Controller createController(final boolean interactive, final PartyMorseCharacterIterator it,
			final Player player, final StatsStore statsStore) {
		return interactive ? new InteractiveController(it, player, statsStore) : new NonInteractiveController(it, player);
	}
}
