package org.devzendo.morsetrainer2.player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class WavAppender {
	private final FileOutputStream fileOutputStream;

	public WavAppender(final File outputFile, final AudioFormat format) throws IOException {
		if (!format.getEncoding().equals(Encoding.PCM_SIGNED)) {
			throw new IllegalArgumentException("Only PCM_SIGNED data supported at this time");
		}

		fileOutputStream = new FileOutputStream(outputFile);

	}

	public void append(final byte[] waveform) {
		// TODO Auto-generated method stub

	}

	public void close() {
		// TODO Auto-generated method stub

	}

}
