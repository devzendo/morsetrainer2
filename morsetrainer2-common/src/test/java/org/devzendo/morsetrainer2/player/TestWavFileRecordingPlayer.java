package org.devzendo.morsetrainer2.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.devzendo.commoncode.string.HexDump;
import org.devzendo.morsetrainer2.logging.LoggingUnittest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestWavFileRecordingPlayer {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestWavFileRecordingPlayer.class);

	@Rule
	public final TemporaryFolder tempDir = new TemporaryFolder();
	private File tempFile;
	private Player player;

	@BeforeClass
	public static void setupLogging() {
		LoggingUnittest.initialise();
	}

	@Before
	public void setupTempfile() throws IOException {
		tempDir.create();
		final File root = tempDir.getRoot();
		tempFile = File.createTempFile("morse", ".wav", root);
		tempFile.deleteOnExit();
		LOGGER.info("Test file is at " + tempFile.getAbsolutePath());

		player = new WavFileRecordingPlayer(600, 20, 20, tempFile);
	}

	@After
	public void deleteTempFile() {
		tempFile.delete();
		assertThat(tempFile.exists(), equalTo(false));
	}

	@Test
	public void emptyRecordingHasJustAHeader() throws IOException {
		player.finish();

		assertThat(tempFile.exists(), equalTo(true));
		assertThat(tempFile.length(), equalTo(44L));
		assertThat(mainChunkSize(), equalTo(4L + 8L + 16L + 8L));
		assertThat(dataChunkSize(), equalTo(0L));
	}

	@Test
	public void aRecordingIsGenerated() throws IOException {
		player.play("ABC");
		player.finish();

		assertThat(tempFile.exists(), equalTo(true));
		assertThat(tempFile.length(), equalTo(29804L));
		assertThat(mainChunkSize(), equalTo(4L + 8L + 16L + 8L + 59520L));
		assertThat(dataChunkSize(), equalTo(59520L));
	}

	private static long getLE(final byte[] buffer, int pos, int numBytes) {
		numBytes--;
		pos += numBytes;

		long val = buffer[pos] & 0xFF;
		for (int b = 0; b < numBytes; b++)
			val = (val << 8) + (buffer[--pos] & 0xFF);

		return val;
	}

	private long dataChunkSize() throws IOException {
		return readLongFromOffset(40L);
	}

	private long mainChunkSize() throws FileNotFoundException, IOException {
		return readLongFromOffset(4L);
	}

	private long readLongFromOffset(final long offset) throws IOException, FileNotFoundException {
		try (final RandomAccessFile raf = new RandomAccessFile(tempFile, "r")) {
			raf.seek(offset);
			final byte buffer[] = new byte[4];
			raf.read(buffer);

			final String[] hexDump = HexDump.hexDump(buffer);
			for (final String string : hexDump) {
				LOGGER.info(string);
			}

			return getLE(buffer, 0, 4);
		}
	}

}

