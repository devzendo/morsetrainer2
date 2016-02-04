package org.devzendo.morsetrainer2.symbol;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Optional;

import org.junit.Test;

public class TestMorseCharacter {

	@Test
	public void testName() {
		assertThat(MorseCharacter.KN.name(), equalTo("KN"));
	}

	@Test
	public void testToString() {
		assertThat(MorseCharacter.KN.toString(), equalTo("<KN>"));
	}

	@Test
	public void testValidFromString() {
		assertThat(MorseCharacter.fromString("<KN>"), equalTo(Optional.of(MorseCharacter.KN)));
		assertThat(MorseCharacter.fromString("a"), equalTo(Optional.of(MorseCharacter.A)));
		assertThat(MorseCharacter.fromString("S"), equalTo(Optional.of(MorseCharacter.S)));
		assertThat(MorseCharacter.fromString("?"), equalTo(Optional.of(MorseCharacter.QUESTION)));
		assertThat(MorseCharacter.fromString("="), equalTo(Optional.of(MorseCharacter.EQUAL)));
		assertThat(MorseCharacter.fromString("7"), equalTo(Optional.of(MorseCharacter.D7)));
	}

	@Test
	public void testInvalidFromString() {
		assertThat(MorseCharacter.fromString("KN"), equalTo(Optional.empty()));
		assertThat(MorseCharacter.fromString(null), equalTo(Optional.empty()));
		assertThat(MorseCharacter.fromString(""), equalTo(Optional.empty()));
		assertThat(MorseCharacter.fromString("  "), equalTo(Optional.empty()));
	}

	@Test
	public void testToPulses() {
		assertThat(MorseCharacter.KN.getPulses(), equalTo(new Pulse[] {Pulse.dah, Pulse.dit, Pulse.dah, Pulse.dah, Pulse.dit}));
		assertThat(MorseCharacter.A.getPulses(), equalTo(new Pulse[] {Pulse.dit, Pulse.dah}));
	}
}
