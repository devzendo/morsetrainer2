package org.devzendo.morsetrainer2.cmd;

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

	@Test
	public void wpmAsLast() {
		constructWithFailure("-wpm must be followed by a speed in words per minute", "-wpm");
	}

	@Test
	public void wpmWithNonInteger() {
		constructWithFailure("-wpm must be followed by a speed in words per minute", "-wpm", "zounds");
	}

	@Test
	public void wpmUnderRange() {
		constructWithFailure("-wpm must be followed by a speed in words per minute in the range 12 to 60", "-wpm", "11");
	}

	@Test
	public void wpmOverRange() {
		constructWithFailure("-wpm must be followed by a speed in words per minute in the range 12 to 60", "-wpm", "61");
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
