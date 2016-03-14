package org.devzendo.morsetrainer2.sound;

import org.apache.log4j.BasicConfigurator;
import org.devzendo.morsetrainer2.player.Player;
import org.devzendo.morsetrainer2.player.SpeakerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayMorseString {
	private static Logger LOGGER = LoggerFactory.getLogger(PlayMorseSound.class);

	public static void main(final String[] args) throws InterruptedException {
		BasicConfigurator.configure();
		final Player player = new SpeakerPlayer(600,  20,  20);
//		final Player player = new WavFileRecordingPlayer(600,  20,  20, new File("/tmp/morse.wav"));
		for(final String arg: args) {
			player.play(arg);
			player.play(" ");
		}
		player.finish();
	}
}
