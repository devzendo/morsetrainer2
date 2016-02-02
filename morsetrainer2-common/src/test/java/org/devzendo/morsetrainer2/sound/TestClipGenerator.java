package org.devzendo.morsetrainer2.sound;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import org.devzendo.morsetrainer2.logging.LoggingUnittest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClipGenerator {
	private static Logger LOGGER = LoggerFactory.getLogger(TestClipGenerator.class);

	@BeforeClass
	public static void setupLogging() {
		LoggingUnittest.initialise();
	}
	
    @Test
    public void farnsworthSpacing() {
        final ClipGenerator cg = new ClipGenerator(18, 12, 600);
        LOGGER.info("farnsworthSpacing");
        LOGGER.info("ditMs: " + cg.ditMs());
        LOGGER.info("dahMs: " + cg.dahMs());
        LOGGER.info("elementSpaceMs: " + cg.elementSpaceMs());
        LOGGER.info("characterSpaceMs: " + cg.characterSpaceMs());
        LOGGER.info("wordSpaceMs: " + cg.wordSpaceMs());
        assertThat(cg.characterSpaceMs(), greaterThan(cg.dahMs()));
    }

    @Test
    public void nonFarnsworthSpacing() {
        final ClipGenerator cg = new ClipGenerator(18, 18, 600);
        LOGGER.info("nonFarnsworthSpacing");
        LOGGER.info("ditMs: " + cg.ditMs());
        LOGGER.info("dahMs: " + cg.dahMs());
        LOGGER.info("elementSpaceMs: " + cg.elementSpaceMs());
        LOGGER.info("characterSpaceMs: " + cg.characterSpaceMs());
        LOGGER.info("wordSpaceMs: " + cg.wordSpaceMs());
        assertThat(cg.characterSpaceMs(), equalTo(cg.dahMs()));
    }
}
