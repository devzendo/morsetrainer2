package org.devzendo.morsetrainer2.player;

import java.io.File;
import java.util.Optional;

public class PlayerFactory {

	public static Player createPlayer(final Integer freqHz, final Integer wpm, final Integer fwpm, final Optional<File> recordFile) {
		if (recordFile.isPresent()) {
			return new WavFileRecordingPlayer(freqHz, wpm, fwpm, recordFile.get());
		} else {
			return new SpeakerPlayer(freqHz, wpm, fwpm);
		}
	}
}
