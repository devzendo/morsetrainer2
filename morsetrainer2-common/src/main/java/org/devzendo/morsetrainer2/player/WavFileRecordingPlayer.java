package org.devzendo.morsetrainer2.player;

import java.io.File;
import java.util.List;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;

public class WavFileRecordingPlayer extends AbstractPlayer implements Player {

	public WavFileRecordingPlayer(final Integer freqHz, final Integer wpm, final Integer fwpm, final File file) {
		super(freqHz, wpm, fwpm);
	}

	@Override
	protected void playMorseCharacters(final List<MorseCharacter> morseChars) {
		// TODO Auto-generated method stub

	}

}
