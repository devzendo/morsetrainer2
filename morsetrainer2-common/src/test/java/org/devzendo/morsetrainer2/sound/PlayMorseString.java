package org.devzendo.morsetrainer2.sound;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.devzendo.morsetrainer2.symbol.Pulse;
import org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser;
import org.devzendo.morsetrainer2.xlat.MorseCharactersToPulses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayMorseString {
	private static Logger LOGGER = LoggerFactory.getLogger(PlayMorseSound.class);
	private final ClipGenerator clipGen;
	private final MorseCharactersToPulses morseCharactersToPulses;
	private final ClipPlayer clipPlayer;

	public static void main(final String[] args) throws InterruptedException {
		BasicConfigurator.configure();
		final PlayMorseString playMorseString = new PlayMorseString();
		for(final String arg: args) {
			playMorseString.playString(arg);
			playMorseString.playString(" ");
		}
	}

	public PlayMorseString() throws InterruptedException {
		LOGGER.info("Starting ClipGenerator");
		clipGen = new ClipGenerator(20, 20, 600);
		morseCharactersToPulses = new MorseCharactersToPulses();
		clipPlayer = new ClipPlayer();
	}

	public void playString(final String message) throws InterruptedException {
		clipGen.clearWaveform();
		final List<Pulse> translate = morseCharactersToPulses.translate(TextToMorseCharacterParser.parseToList(message));
		for (final Pulse pulse : translate) {
			clipGen.addToWaveform(pulse);
		}
		clipPlayer.playSynchronously(clipGen.getWaveform());
	}
}
