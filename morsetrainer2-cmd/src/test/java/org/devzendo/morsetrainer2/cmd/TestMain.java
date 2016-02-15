package org.devzendo.morsetrainer2.cmd;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Properties;

import org.devzendo.morsetrainer2.logging.LoggingUnittest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestMain {
	private static Properties properties = Main.getPropertiesResource();
	
	@BeforeClass
	public static void setupLogging() {
		LoggingUnittest.initialise();
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// WPM
	
	@Test
	public void wpmAsLast() {
		constructWithFailure("-wpm must be in the range 12 to 60", "-wpm");
	}

	@Test
	public void wpmWithNonInteger() {
		constructWithFailure("-wpm must be in the range 12 to 60", "-wpm", "zounds");
	}

	@Test
	public void wpmUnderRange() {
		constructWithFailure("-wpm must be in the range 12 to 60", "-wpm", "11");
	}

	@Test
	public void wpmOverRange() {
		constructWithFailure("-wpm must be in the range 12 to 60", "-wpm", "61");
	}

	// FWPM
	
	@Test
	public void fwpmAsLast() {
		constructWithFailure("-fwpm must be in the range 12 to 60", "-fwpm");
	}

	@Test
	public void fwpmWithNonInteger() {
		constructWithFailure("-fwpm must be in the range 12 to 60", "-fwpm", "zounds");
	}

	@Test
	public void fwpmUnderRange() {
		constructWithFailure("-fwpm must be in the range 12 to 60", "-fwpm", "11");
	}

	@Test
	public void fwpmOverRange() {
		constructWithFailure("-fwpm must be in the range 12 to 60", "-fwpm", "61");
	}

	// FREQ
	
	@Test
	public void freqAsLast() {
		constructWithFailure("-freq must be in the range 400 to 800", "-freq");
	}

	@Test
	public void freqWithNonInteger() {
		constructWithFailure("-freq must be in the range 400 to 800", "-freq", "zounds");
	}

	@Test
	public void freqUnderRange() {
		constructWithFailure("-freq must be in the range 400 to 800", "-freq", "399");
	}

	@Test
	public void freqOverRange() {
		constructWithFailure("-freq must be in the range 400 to 800", "-freq", "801");
	}

	// full options
	@Test
	public void fwpmSameAsWpmIfNotGiven() throws Exception {
		final Options options = construct("-wpm", "17").getOptions();
		assertThat(options.wpm, equalTo(new Integer("17")));
		assertThat(options.fwpm, equalTo(new Integer("17")));
	}

	@Test
	public void freqDefaultedInIfNotGiven() throws Exception {
		final Options options = construct("-wpm", "17").getOptions();
		assertThat(options.freqHz, equalTo(new Integer("600")));
	}

	@Test
	public void wpmAndFwpmAndFreqDefaultedInIfNotGiven() throws Exception {
		final Options options = construct().getOptions();
		assertThat(options.wpm, equalTo(new Integer("12")));
		assertThat(options.fwpm, equalTo(new Integer("12")));
		assertThat(options.freqHz, equalTo(new Integer("600")));
	}

	@Test
	public void fwpmCanBeVaried() throws Exception {
		final Options options = construct("-fwpm", "17").getOptions();
		assertThat(options.wpm, equalTo(new Integer("12")));
		assertThat(options.fwpm, equalTo(new Integer("17")));
	}
	
	private void constructWithFailure(final String message, final String ... args) {
		thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(message);
        construct(args);
	}

	private Main construct(final String ... args) {
		return new Main(Arrays.asList(args), properties);
	}
}
