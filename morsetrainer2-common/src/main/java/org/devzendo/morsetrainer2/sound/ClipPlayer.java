package org.devzendo.morsetrainer2.sound;

import java.util.concurrent.CountDownLatch;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.LineListener;

public class ClipPlayer {
	private static Logger LOGGER = LoggerFactory.getLogger(ClipPlayer.class);

	public void playSynchronously(final Clip clip) throws InterruptedException {
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

}
