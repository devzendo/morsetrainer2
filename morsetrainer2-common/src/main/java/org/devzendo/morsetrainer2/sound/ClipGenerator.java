package org.devzendo.morsetrainer2.sound;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import org.devzendo.morsetrainer2.symbol.Pulse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClipGenerator {
	private static final boolean IS_BIG_ENDIAN = false;
	private static final int CHANNELS = 1;
	private static final int SAMPLE_SIZE_IN_BITS = 16;
	private static final int FRAME_SIZE = 2;
	private static final int SAMPLE_RATE = 8000;
	private static final Double TWO_PI = Math.PI * 2.0;
	private static final Logger LOGGER = LoggerFactory.getLogger(ClipGenerator.class);
	private static final AudioFormat FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE,
			SAMPLE_SIZE_IN_BITS, CHANNELS, FRAME_SIZE, SAMPLE_RATE, IS_BIG_ENDIAN);

	private double ditDurationSeconds = 0.0;
	private Clip ditClip = null;
	private Clip dahClip = null;
	private Clip elementSpaceClip = null;
	private Clip characterSpaceClip = null;
	private Clip wordSpaceClip = null;
	private long ditMs;
	private long dahMs;
	private long elementSpaceMs;
	private long characterSpaceMs;
	private long wordSpaceMs;
	private int wpm;
	private int fwpm;
	private int freqHz;

	private byte[] dit;
	private byte[] dah;
	private byte[] elementSpace;
	private byte[] characterSpace;
	private byte[] wordSpace;

	private final List<Pulse> waveformPulses = new ArrayList<>();
	private int waveformPulseLength = 0;

	public static AudioFormat getFormat() {
		return FORMAT;
	}

	public ClipGenerator(final int wpm, final int fwpm, final int freqHz) {
		this.wpm = wpm;
		this.fwpm = fwpm;
		this.freqHz = freqHz;
		initialise();
		initialiseSpacing();
	}

	public void setWpm(final int w) {
		wpm = w;
		initialise();
	}

	public void setFarnsworthWpm(final int w) {
		fwpm = w;
		initialiseSpacing();
	}

	public void setFrequency(final int hz) {
		freqHz = hz;
		initialise();
	}

	public Clip getDit() {
		return ditClip;
	}

	public Clip getDah() {
		return dahClip;
	}

	public Clip getElementSpace() {
		return elementSpaceClip;
	}

	public Clip getCharacterSpace() {
		return characterSpaceClip;
	}

	public Clip getWordSpace() {
		return wordSpaceClip;
	}

	public Clip translate(final Pulse pulse) {
		switch (pulse) {
		case dit:
			return getDit();
		case dah:
			return getDah();
		case ch:
			return getCharacterSpace();
		case el:
			return getElementSpace();
		default: // sealed case classes FTW!
		case wo:
			return getWordSpace();
		}
	}

	public void clearWaveform() {
		waveformPulses.clear();
		waveformPulseLength = 0;
	}

	public void addToWaveform(final Pulse pulse) {
		waveformPulses.add(pulse);
		switch (pulse) {
		case dit:
			waveformPulseLength += dit.length;
			break;
		case dah:
			waveformPulseLength += dah.length;
			break;
		case ch:
			waveformPulseLength += characterSpace.length;
			break;
		case el:
			waveformPulseLength += elementSpace.length;
			break;
		default: // sealed case classes FTW!
		case wo:
			waveformPulseLength += wordSpace.length;
			break;
		}
	}

	public Clip getWaveform() {
		final byte[] samples = getRawWaveform();
		return samplesToClip(samples);
	}

	public byte[] getRawWaveform() {
		final byte[] samples = new byte[waveformPulseLength];
		int index = 0;
		for (final Pulse pulse : waveformPulses) {
			switch (pulse) {
			case dit:
				System.arraycopy(dit, 0, samples, index, dit.length);
				index += dit.length;
				break;
			case dah:
				System.arraycopy(dah, 0, samples, index, dah.length);
				index += dah.length;
				break;
			case ch:
				System.arraycopy(characterSpace, 0, samples, index, characterSpace.length);
				index += characterSpace.length;
				break;
			case el:
				System.arraycopy(elementSpace, 0, samples, index, elementSpace.length);
				index += elementSpace.length;
				break;
			default: // sealed case classes FTW!
			case wo:
				System.arraycopy(wordSpace, 0, samples, index, wordSpace.length);
				index += wordSpace.length;
				break;
			}
		}
		return samples;
	}

	private Clip samplesToClip(final byte[] out) {
		try {
			final Clip clip = AudioSystem.getClip();
			clip.open(FORMAT, out, 0, out.length);
			clip.setFramePosition(0);
			return clip;
		} catch (final LineUnavailableException e) {
			final String msg = "Cannot convert waveform of bytes to a Clip: " + e.getMessage();
			LOGGER.error(msg, e);
			throw new IllegalStateException(msg, e);
		}
	}

	private void initialise() {
		// http://sv8gxc.blogspot.co.uk/2010/09/morse-code-101-in-wpm-bw-snr.html
		ditDurationSeconds = wpmToSeconds(wpm);
		ditMs = (long) (ditDurationSeconds * 1000.0);
		dit = createPulse(ditDurationSeconds);
		ditClip = samplesToClip(dit);
		dah = createPulse(ditDurationSeconds * 3.0);
		dahClip = samplesToClip(dah);
		dahMs = ditMs * 3;
		elementSpace = createSilence(ditDurationSeconds);
		elementSpaceClip = samplesToClip(elementSpace);
		elementSpaceMs = ditMs;
		LOGGER.debug("ditMs: " + ditMs + " dahMs: " + dahMs + " elSp: " + elementSpaceMs);
	}

	private void initialiseSpacing() {
		final double farnsworthDitDurationSeconds = wpmToSeconds(fwpm);
		final long farnsworthDitDurationMs = (long) (farnsworthDitDurationSeconds * 1000.0);
		characterSpace = createSilence(farnsworthDitDurationSeconds * 3.0);
		characterSpaceClip = samplesToClip(characterSpace);
		characterSpaceMs = farnsworthDitDurationMs * 3;
		wordSpace = createSilence(farnsworthDitDurationSeconds * 7.0);
		wordSpaceClip = samplesToClip(wordSpace);
		wordSpaceMs = farnsworthDitDurationMs * 7;
		LOGGER.debug("charSp: " + characterSpaceMs + " wdSp: " + wordSpaceMs);
	}

	private byte[] createSilence(final double durationSeconds) {
		return new byte[(int) (ClipGenerator.SAMPLE_RATE * durationSeconds) * 2];
	}

	private byte[] createPulse(final double durationSeconds) {
		final int samples = (int) (ClipGenerator.SAMPLE_RATE * durationSeconds);
		// must fill in sin(0) to sin(2pi) freqHz times in SAMPLE_RATE bytes

		// Create sine wave without ramp-up/down
		final byte[] out = createSine(samples, freqHz);

		// Ramp up at start and down at end
		ramp(samples, out, wpm);
		return out;
	}

	private void ramp(final int samples, final byte[] out, final int wpm) {
		final double rampDurationSeconds = ditDurationSeconds / 8.0;
		// should probably be based on the sample rate? 8 sounds good at 20WPM.
		final int rampSamples = (int) (ClipGenerator.SAMPLE_RATE * rampDurationSeconds);
		final float dRampSamples = rampSamples;
		for (int i = 0, j = 0; i < rampSamples; i++, j += 2) {
			scaleBy(out, j, i / dRampSamples);
		}
		for (int i = (rampSamples - 1), j = (samples - rampSamples) << 1; i >= 0; i --, j += 2) {
			scaleBy(out, j, i / dRampSamples);
		}
	}

	private void scaleBy(final byte[] out, final int idx, final float scale) {
		short samp = (short)(out[idx + 1] & 0x00ff);
		samp <<= 8;
		samp |= (out[idx] & 0x00ff);
		samp *= scale;
		out[idx + 1] = (byte) ((samp & 0xff00) >> 8);
		out[idx] = (byte) (samp & 0x00ff);
	}

	private byte[] createSine(final int samples, final int freqHz) {
		final double cycleLength = ClipGenerator.SAMPLE_RATE / freqHz;

		final int twoSamples = samples * 2;
		final byte[] out = new byte[twoSamples];
		for (int j = 0, i = 0; i < twoSamples; i += 2, j++) {
			final double prop = ((j % cycleLength) / cycleLength) * ClipGenerator.TWO_PI;
			final double value = Math.sin(prop);
			final int iVal = (int) (value * 32767) + 65536;
			out[i + 1] = (byte) ((iVal & 0xff00) >> 8);
			out[i] = (byte) (iVal & 0x00ff);
		}
		return out;
	}

	private double wpmToSeconds(final int wpm) {
		return 1.2 / wpm;
	}

	public long ditMs() {
		return ditMs;
	}

	public long dahMs() {
		return dahMs;
	}

	public long elementSpaceMs() {
		return elementSpaceMs;
	}

	public long characterSpaceMs() {
		return characterSpaceMs;
	}

	public long wordSpaceMs() {
		return wordSpaceMs;
	}
}
