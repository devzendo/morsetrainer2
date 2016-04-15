package org.devzendo.morsetrainer2.qso;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Iterator;

import org.devzendo.morsetrainer2.logging.LoggingUnittest;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCallsignGenerator {

	@BeforeClass
	public static void setupLogging() {
		LoggingUnittest.initialise();
	}

	@Test
	public void zeroGroupCallsigns() throws Exception {
		final CallsignGenerator gen = new CallsignGenerator(0);
		final String all = extract(gen);
		assertThat(all.length(), equalTo(0));
	}

	@Test
	public void generateCallsigns() throws Exception {
		final CallsignGenerator gen = new CallsignGenerator(5);
		final String all = extract(gen);
		System.out.println("Callsigns: [" + all + "]");
	}

	private String extract(final CallsignGenerator gen) {
		final StringBuilder sb = new StringBuilder();
		final Iterator<PartyMorseCharacter> it = gen.iterator();
		while (it.hasNext()) {
			final PartyMorseCharacter next = it.next();
			final String string = next.getRight().toString();
			sb.append(string);
		}
		final String all = sb.toString();
		return all;
	}
}
