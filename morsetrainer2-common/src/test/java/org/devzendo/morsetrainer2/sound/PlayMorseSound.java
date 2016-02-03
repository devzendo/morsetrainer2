package org.devzendo.morsetrainer2.sound;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayMorseSound {
	private static Logger LOGGER = LoggerFactory.getLogger(PlayMorseSound.class);

	public static void main(final String[] args) throws InterruptedException {
		BasicConfigurator.configure();

		LOGGER.info("Starting ClipGenerator");
		final ClipGenerator clipGen = new ClipGenerator(20, 20, 600);
		final ClipPlayer clipPlayer = new ClipPlayer();
		LOGGER.info("Playing...");
		for (int i = 0; i < 10; i++) {
			clipPlayer.playSynchronously(clipGen.getDit());
			clipPlayer.playSynchronously(clipGen.getElementSpace());
			clipPlayer.playSynchronously(clipGen.getDah());
			clipPlayer.playSynchronously(clipGen.getElementSpace());
			clipPlayer.playSynchronously(clipGen.getDit());
			clipPlayer.playSynchronously(clipGen.getElementSpace());
			clipPlayer.playSynchronously(clipGen.getDit());
			clipPlayer.playSynchronously(clipGen.getCharacterSpace());
		}
		LOGGER.info("Finished playing");
	}
}
