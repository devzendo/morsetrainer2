package org.devzendo.morsetrainer2.player;

import java.util.ArrayList;
import java.util.List;

import org.devzendo.morsetrainer2.sound.ClipGenerator;
import org.devzendo.morsetrainer2.sound.ClipPlayer;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;
import org.devzendo.morsetrainer2.symbol.Pulse;
import org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser;
import org.devzendo.morsetrainer2.xlat.MorseCharactersToPulses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeakerPlayer implements Player {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpeakerPlayer.class);

	private final ClipGenerator clipGen;
	private final MorseCharactersToPulses morseCharactersToPulses;
	private final ClipPlayer clipPlayer;

	public SpeakerPlayer(final Integer freqHz, final Integer wpm, final Integer fwpm) {
		clipGen = new ClipGenerator(wpm, fwpm, freqHz);
		morseCharactersToPulses = new MorseCharactersToPulses();
		clipPlayer = new ClipPlayer();
	}

	@Override
	public void play(final String anyString) {
		// TODO the party is not here, can't play by the correct clipPlayer
		final List<MorseCharacter> morseChars = TextToMorseCharacterParser.parseToList(anyString);
		playMorseCharacters(morseChars);
	}

	@Override
	public void play(final PartyMorseCharacter... chars) {
		final List<MorseCharacter> morseChars = new ArrayList<>();
		for (int i = 0; i < chars.length; i++) {
			final MorseCharacter ch = chars[i].getRight();
			morseChars.add(ch);
		}
		playMorseCharacters(morseChars);
	}

	private void playMorseCharacters(final List<MorseCharacter> morseChars) {
		try {
			// TODO take party into account...
			clipGen.clearWaveform();
			for (final Pulse pulse : morseCharactersToPulses.translate(morseChars)) {
				clipGen.addToWaveform(pulse);
			}
			clipPlayer.playSynchronously(clipGen.getWaveform());
		} catch (final InterruptedException e) {
			LOGGER.warn("Interrupted when playing: " + e.getMessage());
		}
	}
}
