package org.devzendo.morsetrainer2.player;

import java.util.List;

import org.devzendo.morsetrainer2.sound.ClipPlayer;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeakerPlayer extends AbstractPlayer implements Player {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpeakerPlayer.class);

	private final ClipPlayer clipPlayer;

	public SpeakerPlayer(final Integer freqHz, final Integer wpm, final Integer fwpm) {
		super(freqHz, wpm, fwpm);
		clipPlayer = new ClipPlayer();
	}

	@Override
	protected void playMorseCharacters(final List<MorseCharacter> morseChars) {
		try {
			buildWaveform(morseChars);
			clipPlayer.playSynchronously(clipGen.getWaveform());
		} catch (final InterruptedException e) {
			LOGGER.warn("Interrupted when playing: " + e.getMessage());
		}
	}

	@Override
	public void finish() {
		// nothing to do in the speaker variant
	}
}
