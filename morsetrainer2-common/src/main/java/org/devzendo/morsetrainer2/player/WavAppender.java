package org.devzendo.morsetrainer2.player;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

/**
 * I am grateful for the WavFile library by A.Greensted, at http://www.labbookpages.co.uk
 * from which this class had its inspiration. That's a more general wav IO class than this.
 *
 * Also, see
 * http://web.archive.org/web/20070329121414/http://ccrma.stanford.edu/CCRMA/Courses/422/projects/WaveFormat/
 * an archived page that explains the WAV format.
 *
 */
public class WavAppender {
	private final static int FMT_CHUNK_ID = 0x20746D66;
	private final static int DATA_CHUNK_ID = 0x61746164;
	private final static int RIFF_CHUNK_ID = 0x46464952;
	private final static int RIFF_TYPE_ID = 0x45564157;
	private final static int BUFFER_SIZE = 4096;

	private final byte[] buffer; // Local buffer used for IO
	private final AudioFormat format;
	private final RandomAccessFile randomAccessFile;
	private int numFrames;

	public WavAppender(final File outputFile, final AudioFormat format) throws IOException {
		this.format = format;
		if (!format.getEncoding().equals(Encoding.PCM_SIGNED)) {
			throw new IllegalArgumentException("Only PCM_SIGNED data supported at this time");
		}

		randomAccessFile = new RandomAccessFile(outputFile, "rw");

		buffer = new byte[BUFFER_SIZE];
		numFrames = 0;

		writeHeader();
	}

	private void writeHeader() throws IOException {
		randomAccessFile.seek(0L);

		putLE(RIFF_CHUNK_ID, 0, 4);
		// Calculate the chunk sizes

		final int sampleSizeInBits = format.getSampleSizeInBits();
		final int blockAlign = (sampleSizeInBits/8) * format.getChannels();
		System.out.println("blockAlign is " + blockAlign);
		System.out.println("sample size is " + format.getSampleSizeInBits());
		System.out.println("channels is " + format.getChannels());
		// 2 bytes unsigned, 0x0001 (1) to 0xFFFF (65,535)

		final long dataChunkSize = blockAlign * numFrames;
		long mainChunkSize =	4 +	// Riff Type
									8 +	// Format ID and size
									16 +	// Format data
									8 + 	// Data ID and size
									dataChunkSize;

		// Chunks must be word aligned, so if odd number of audio data bytes
		// adjust the main chunk size
		if (dataChunkSize % 2 == 1) {
			mainChunkSize += 1;
		}

		putLE(mainChunkSize, 4, 4); // update this
		putLE(RIFF_TYPE_ID, 8, 4);

		// Write out the header
		randomAccessFile.write(buffer, 0, 12);

		// Put format data in buffer
		final long averageBytesPerSecond = (long) (format.getSampleRate() * blockAlign);

		putLE(FMT_CHUNK_ID,                  0, 4);		// Chunk ID
		putLE(16,                            4, 4);		// Chunk Data Size
		putLE(1,                             8, 2);		// Compression Code (Uncompressed)
		putLE(format.getChannels(),          10, 2);		// Number of channels
		putLE((long) format.getSampleRate(), 12, 4);		// Sample Rate
		putLE(averageBytesPerSecond,         16, 4);		// Average Bytes Per Second
		putLE(blockAlign,                    20, 2);		// Block Align
		putLE(format.getSampleSizeInBits(),  22, 2);		// Bits per sample

		// Write Format Chunk
		randomAccessFile.write(buffer, 0, 24);

		// Start Data Chunk
		putLE(DATA_CHUNK_ID, 0, 4);		// Chunk ID
		putLE(dataChunkSize, 4, 4);		// Chunk Data Size

		// Write Format Chunk
		randomAccessFile.write(buffer, 0, 8);
	}

	private void putLE(long val, int pos, final int numBytes) {
		for (int b = 0; b < numBytes; b++) {
			buffer[pos] = (byte) (val & 0xFF);
			val >>= 8;
			pos++;
		}
	}

	public void append(final byte[] waveform) throws IOException {
		// precondition: after initial open, we've written the header once, so are ready to append the raw waveform.
		randomAccessFile.write(waveform);
		numFrames += waveform.length;
		System.out.println("numframes is " + numFrames);
	}

	public void close() throws IOException {
		writeHeader();
		randomAccessFile.close();
	}
}
