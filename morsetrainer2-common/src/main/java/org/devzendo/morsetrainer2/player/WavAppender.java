package org.devzendo.morsetrainer2.player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;

public class WavAppender {
	private final FileOutputStream fileOutputStream;

	public WavAppender(final File outputFile, final AudioFormat format) throws IOException {
		fileOutputStream = new FileOutputStream(outputFile);
	}

	public void append(final byte[] waveform) {
		// TODO Auto-generated method stub

	}

	public void close() {
		// TODO Auto-generated method stub

	}

}
