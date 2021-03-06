package org.devzendo.morsetrainer2.player;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import org.devzendo.morsetrainer2.sound.ClipGenerator;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WavFileRecordingPlayer extends AbstractPlayer implements Player {

	private static Logger LOGGER = LoggerFactory.getLogger(WavFileRecordingPlayer.class);

	private final WavAppender wavAppender;

	public WavFileRecordingPlayer(final Integer freqHz, final Integer wpm, final Integer fwpm, final File wavFile) {
		super(freqHz, wpm, fwpm);

		try {
			final AudioFormat format = ClipGenerator.getFormat();
			wavAppender = new WavAppender(wavFile, format);
		} catch (final IOException e) {
			final String msg = "Cannot create recording file: " + e.getMessage();
			LOGGER.error(msg);
			throw new IllegalStateException(msg, e);
		}
	}

	@Override
	protected void playMorseCharacters(final List<MorseCharacter> morseChars) {
		buildWaveform(morseChars);
		try {
			wavAppender.append(clipGen.getRawWaveform());
		} catch (final IOException e) {
			final String msg = "Cannot close recording file: " + e.getMessage();
			LOGGER.error(msg);
			throw new IllegalStateException(msg, e);
		}
	}

	@Override
	public void finish() {
		try {
			wavAppender.close();
		} catch (final IOException e) {
			final String msg = "Cannot close recording file: " + e.getMessage();
			LOGGER.error(msg);
			throw new IllegalStateException(msg, e);
		}
	}
}
