package org.devzendo.morsetrainer2.sound;

import java.util.concurrent.CountDownLatch;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayMorseSound {
	private static Logger LOGGER = LoggerFactory.getLogger(PlayMorseSound.class);

	public static void playSynchronously(final Clip clip) throws InterruptedException {
		//LOGGER.debug("playing clip " + clip);
		final CountDownLatch latch = new CountDownLatch(1);
		
		final LineListener listener = new LineListener() {
			public void update(LineEvent event) {
				Type eventType = event.getType();
				//System.out.println("event " + eventType);
				if (eventType == Type.STOP || eventType == Type.CLOSE) {
					latch.countDown();;
				}
			}};
		clip.addLineListener(listener);
		clip.setFramePosition(0);

		clip.start();
		//clip.drain(); // drain works ish on 1.6 but seems to return too fast on 1.7/1.8
		latch.await();
		clip.removeLineListener(listener);
	}

	public static void main(final String[] args) throws InterruptedException {
		BasicConfigurator.configure();

		LOGGER.info("Starting ClipGenerator");
		final ClipGenerator clipGen = new ClipGenerator(20, 20, 600);
		LOGGER.info("Playing...");
		for (int i = 0; i < 10; i++) {
			playSynchronously(clipGen.getDit());
			playSynchronously(clipGen.getElementSpace());
			playSynchronously(clipGen.getDah());
			playSynchronously(clipGen.getElementSpace());
			playSynchronously(clipGen.getDit());
			playSynchronously(clipGen.getElementSpace());
			playSynchronously(clipGen.getDit());
			playSynchronously(clipGen.getCharacterSpace());
		}
		LOGGER.info("Finished playing");
	}
}
