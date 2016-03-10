package org.devzendo.morsetrainer2.player;

import java.util.ArrayList;
import java.util.List;

import org.devzendo.morsetrainer2.sound.ClipGenerator;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;
import org.devzendo.morsetrainer2.symbol.Pulse;
import org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser;
import org.devzendo.morsetrainer2.xlat.MorseCharactersToPulses;

public abstract class AbstractPlayer {

	protected final ClipGenerator clipGen;
	protected final MorseCharactersToPulses morseCharactersToPulses;

	public AbstractPlayer(final Integer freqHz, final Integer wpm, final Integer fwpm) {
		clipGen = new ClipGenerator(wpm, fwpm, freqHz);
		morseCharactersToPulses = new MorseCharactersToPulses();
	}

	public void play(final String anyString) {
		// TODO the party is not here, can't generate clips by the correct clipGen
		final List<MorseCharacter> morseChars = TextToMorseCharacterParser.parseToList(anyString);
		playMorseCharacters(morseChars);
	}

	public void play(final PartyMorseCharacter... chars) {
		final List<MorseCharacter> morseChars = new ArrayList<>();
		for (int i = 0; i < chars.length; i++) {
			final MorseCharacter ch = chars[i].getRight();
			morseChars.add(ch);
		}
		playMorseCharacters(morseChars);
	}

	abstract protected void playMorseCharacters(final List<MorseCharacter> morseChars);

	protected void buildWaveform(final List<MorseCharacter> morseChars) {
		// TODO take party into account...
		clipGen.clearWaveform();
		for (final Pulse pulse : morseCharactersToPulses.translate(morseChars)) {
			clipGen.addToWaveform(pulse);
		}
	}
}
