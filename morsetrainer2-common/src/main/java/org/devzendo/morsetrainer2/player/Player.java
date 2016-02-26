package org.devzendo.morsetrainer2.player;

import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;

public interface Player {

	void play(String anyString);

	void play(PartyMorseCharacter ... chars);

}
