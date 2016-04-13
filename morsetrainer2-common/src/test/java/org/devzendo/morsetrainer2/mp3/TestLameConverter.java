package org.devzendo.morsetrainer2.mp3;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assume.assumeThat;

import java.io.File;
import java.io.IOException;

import org.devzendo.commoncode.os.OSTypeDetect;
import org.devzendo.commoncode.os.OSTypeDetect.OSType;
import org.devzendo.morsetrainer2.logging.LoggingUnittest;
import org.devzendo.morsetrainer2.player.Player;
import org.devzendo.morsetrainer2.player.WavFileRecordingPlayer;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * These tests don't run in Eclipse, /opt/local/bin isn't on the PATH.
 * Runs from command line.
 *
 */
public class TestLameConverter {

	private static final OSType os = OSTypeDetect.getInstance().getOSType();

	@BeforeClass
	public static void setupLogging() {
		LoggingUnittest.initialise();
	}

	@Test
	public void available() {
		macOSXAssumptions();

		assertThat(new LameConverter().converterAvailable(), equalTo(true));
	}

	private void macOSXAssumptions() {
		assumeThat(os, equalTo(OSType.MacOSX));
		final String path = System.getenv("PATH");
		assumeThat(path.contains("/opt/local/bin"), equalTo(true));
	}

	@Test
	public void testConversion() throws Exception {
		macOSXAssumptions();

		final File wavFile = createSampleMorseWav();

		final File mp3File = new LameConverter().convertToMP3(wavFile);
		mp3File.deleteOnExit();

		assertThat(mp3File.exists(), equalTo(true));
	}

	private File createSampleMorseWav() throws IOException {
		final File tempFile = File.createTempFile("morse", ".wav");
		tempFile.deleteOnExit();

		final Player player = new WavFileRecordingPlayer(600, 20, 20, tempFile);
		player.play("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		player.finish();

		return tempFile;
	}
}
