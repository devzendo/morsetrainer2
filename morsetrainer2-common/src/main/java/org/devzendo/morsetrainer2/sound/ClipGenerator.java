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
	private static int FRAME_SIZE = 2;
	private static int SAMPLE_RATE = 8000;
	private static Double TWO_PI = Math.PI * 2.0;
	private static Logger LOGGER = LoggerFactory.getLogger(ClipGenerator.class);
    private static final AudioFormat FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
            SAMPLE_RATE, 8, 1, FRAME_SIZE, SAMPLE_RATE, false);

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
		return new byte[(int) (ClipGenerator.SAMPLE_RATE * durationSeconds)];
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
		for (int i = 0; i < rampSamples; i++) {
			out[i] *= (i / dRampSamples);
		}
		for (int i = (rampSamples - 1); i >= 0; i--) {
			final int idx = samples - i - 1;
			out[idx] *= (i / dRampSamples);
		}
	}

	private byte[] createSine(final int samples, final int freqHz) {
		final int cycleLength = ClipGenerator.SAMPLE_RATE / freqHz;
		final double dCycleLength = cycleLength;

		final byte[] out = new byte[samples];
		for (int i = 0; i < samples; i++) {
			final int x = i % cycleLength;
			final double prop = x / dCycleLength;
			final double sinprop = prop * ClipGenerator.TWO_PI;
			final double value = Math.sin(sinprop);
			final int iVal = (int) (value * 127) + 256;
			out[i] = (byte) (iVal & 0xff);
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
