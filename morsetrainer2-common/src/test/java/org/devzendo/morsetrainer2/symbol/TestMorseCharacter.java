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
		assertThat(MorseCharacter.fromString(" "), equalTo(Optional.of(MorseCharacter.SPC)));
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
	
	@Test
	public void testValidFromChar() {
		assertThat(MorseCharacter.fromChar('a'), equalTo(Optional.of(MorseCharacter.A)));
		assertThat(MorseCharacter.fromChar('S'), equalTo(Optional.of(MorseCharacter.S)));
		assertThat(MorseCharacter.fromChar('?'), equalTo(Optional.of(MorseCharacter.QUESTION)));
		assertThat(MorseCharacter.fromChar('='), equalTo(Optional.of(MorseCharacter.EQUAL)));
		assertThat(MorseCharacter.fromChar('7'), equalTo(Optional.of(MorseCharacter.D7)));
		assertThat(MorseCharacter.fromChar(' '), equalTo(Optional.of(MorseCharacter.SPC)));
	}

	@Test
	public void testInvalidFromChar() {
		assertThat(MorseCharacter.fromChar('~'), equalTo(Optional.empty()));
		assertThat(MorseCharacter.fromChar('<'), equalTo(Optional.empty()));
		assertThat(MorseCharacter.fromChar('>'), equalTo(Optional.empty()));
	}

	@Test
	public void testValidFromProsignText() {
		assertThat(MorseCharacter.fromProsignText("<KN>"), equalTo(Optional.empty()));
		// yes, fromProsignText does not use < >, just text in between them
		
		assertThat(MorseCharacter.fromProsignText("KN"), equalTo(Optional.of(MorseCharacter.KN)));
		assertThat(MorseCharacter.fromProsignText("kn"), equalTo(Optional.of(MorseCharacter.KN)));
		assertThat(MorseCharacter.fromProsignText("qz"), equalTo(Optional.empty()));
		assertThat(MorseCharacter.fromProsignText("k"), equalTo(Optional.empty()));
		assertThat(MorseCharacter.fromProsignText(""), equalTo(Optional.empty()));
		assertThat(MorseCharacter.fromProsignText(null), equalTo(Optional.empty()));
	}
}
