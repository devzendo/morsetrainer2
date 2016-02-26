package org.devzendo.morsetrainer2.controller;

import org.devzendo.morsetrainer2.iterator.PartyMorseCharacterIterator;
import org.devzendo.morsetrainer2.player.Player;

public class ControllerFactory {

	public static Controller createController(final boolean interactive, final PartyMorseCharacterIterator it, final Player player) {
		return interactive ? new InteractiveController(it, player) : new NonInteractiveController(it, player);
	}
}
