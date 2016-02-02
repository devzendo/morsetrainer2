package org.devzendo.morsetrainer2.sound;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

import org.apache.log4j.BasicConfigurator;
import org.devzendo.morsetrainer2.sound.PlaySilence.AudioListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayMorseSound {
	private static Logger LOGGER = LoggerFactory.getLogger(PlayMorseSound.class);

	public static void play(final Clip clip) throws InterruptedException {
		//LOGGER.debug("playing clip " + clip);
		AudioListener listener = new AudioListener();
		clip.addLineListener(listener);
		clip.setFramePosition(0);

		clip.start();
		//clip.drain(); // drain works ish on 1.6 but seems to return too fast on 1.7/1.8
		listener.waitUntilDone();
		clip.removeLineListener(listener);
	}

	static class AudioListener implements LineListener {
		private boolean done = false;

		public synchronized void reset() {
			done = false;
		}

		public synchronized void update(LineEvent event) {
			Type eventType = event.getType();
			//System.out.println("event " + eventType);
			if (eventType == Type.STOP || eventType == Type.CLOSE) {
				done = true;
				notifyAll();
			}
		}

		public synchronized void waitUntilDone() throws InterruptedException {
			while (!done) {
				wait();
			}
		}
	}

	public static void main(final String[] args) throws InterruptedException {
		BasicConfigurator.configure();

		LOGGER.info("Starting ClipGenerator");
		final ClipGenerator clipGen = new ClipGenerator(12, 12, 600);
		LOGGER.info("Playing...");
		for (int i = 0; i < 10; i++) {
			play(clipGen.getDit());
			play(clipGen.getElementSpace());
			play(clipGen.getDah());
			play(clipGen.getElementSpace());
			play(clipGen.getDit());
			play(clipGen.getElementSpace());
			play(clipGen.getDit());
			play(clipGen.getCharacterSpace());
		}
		LOGGER.info("Finished playing");
	}
}
