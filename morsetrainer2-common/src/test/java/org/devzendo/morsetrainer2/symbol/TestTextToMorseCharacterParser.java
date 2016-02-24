package org.devzendo.morsetrainer2.symbol;

import static org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.devzendo.morsetrainer2.logging.LoggingUnittest;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTextToMorseCharacterParser {

	final TextToMorseCharacterParser parser = new TextToMorseCharacterParser();

	@BeforeClass
	public static void setupLogging() {
		LoggingUnittest.initialise();
	}

	// Tests of the static 'full document' parser.
	@Test
	public void emptyString() {
		assertThat(parse(null), arrayWithSize(0));
		assertThat(parse(""), arrayWithSize(0));
	}

	@Test
	public void simples() {
		assertThat(parse("ab c"), equalTo(
				new MorseCharacter[] { MorseCharacter.A, MorseCharacter.B, MorseCharacter.SPC, MorseCharacter.C }));
	}
	
	@Test
	public void spacesAreNotCoalesced() {
		assertThat(parse("  "),
				equalTo(new MorseCharacter[] { MorseCharacter.SPC, MorseCharacter.SPC }));
	}

	@Test
	public void prosigns() {
		assertThat(parse("a<kn><cl>"),
				equalTo(new MorseCharacter[] { MorseCharacter.A, MorseCharacter.KN, MorseCharacter.CL }));
	}

	@Test
	public void parseToString() {
		assertThat(TextToMorseCharacterParser.parseToString("a=<kn>3<cl>."), equalTo("A=<KN>3<CL>."));
	}

	@Test
	public void parseToSetAsArray() {
		final MorseCharacter[] uniqueArray = TextToMorseCharacterParser.parseToSetAsArray("a=a3bc<kn>3<cl>.$");
		assertThat(uniqueArray, arrayWithSize(8));
		assertThat(uniqueArray, arrayContainingInAnyOrder(MorseCharacter.A, MorseCharacter.EQUAL, MorseCharacter.D3,
				MorseCharacter.B, MorseCharacter.C, MorseCharacter.KN, MorseCharacter.CL, MorseCharacter.FULLSTOP));
	}

	@Test
	public void parseTolist() {
		final List<MorseCharacter> list = TextToMorseCharacterParser.parseToList("a=a3bc<kn>3<cl>.$");
		assertThat(list, hasSize(10));
		assertThat(list, Matchers.contains(MorseCharacter.A, MorseCharacter.EQUAL, MorseCharacter.A, MorseCharacter.D3,
				MorseCharacter.B, MorseCharacter.C, MorseCharacter.KN, MorseCharacter.D3,MorseCharacter.CL, MorseCharacter.FULLSTOP));
	}

	@Test
	public void unfinishedProsign() {
		assertThat(parse("a<kn"), equalTo(new MorseCharacter[] { MorseCharacter.A }));
		assertThat(parse(">a"), equalTo(new MorseCharacter[] { MorseCharacter.A }));
		assertThat(parse("<>a"), equalTo(new MorseCharacter[] { MorseCharacter.A }));
		assertThat(parse("><>a<"), equalTo(new MorseCharacter[] { MorseCharacter.A }));
	}

	@Test
	public void unknownsAreIgnored() {
		assertThat(parse("a%b<zz>c"),
				equalTo(new MorseCharacter[] { MorseCharacter.A, MorseCharacter.B, MorseCharacter.C }));
	}

	// More focussed parser tests.
	@Test
	public void initiallyHasNothing() {
		assertThat(parser.hasNext(), equalTo(false));
	}
	
	@Test
	public void goodCharacterIsImmediatelyAvailable() {
		parser.addString("a");
		assertThat(parser.hasNext(), equalTo(true));
		assertThat(parser.next(), equalTo(MorseCharacter.A));
		assertThat(parser.hasNext(), equalTo(false));
		parser.addString("z");
		assertThat(parser.hasNext(), equalTo(true));
		assertThat(parser.next(), equalTo(MorseCharacter.Z));
		assertThat(parser.hasNext(), equalTo(false));
		parser.addString(",");
		assertThat(parser.hasNext(), equalTo(true));
		assertThat(parser.next(), equalTo(MorseCharacter.COMMA));
		assertThat(parser.hasNext(), equalTo(false));
	}
	
	@Test
	public void fragmentedProsignIsNotImmediatelyAvailable() {
		parser.addString("<");
		assertThat(parser.hasNext(), equalTo(false));
		parser.addString("c");
		assertThat(parser.hasNext(), equalTo(false));
		parser.addString("L");
		assertThat(parser.hasNext(), equalTo(false));
		parser.addString(">");
		assertThat(parser.hasNext(), equalTo(true));
		assertThat(parser.next(), equalTo(MorseCharacter.CL));
		assertThat(parser.hasNext(), equalTo(false));
	}
}
