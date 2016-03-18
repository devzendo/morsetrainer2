package org.devzendo.morsetrainer2.symbol;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

public class TestMorseWord {

	@Test
	public void empty() {
		final MorseWord mw = new MorseWord();
		MatcherAssert.assertThat(mw.size(), equalTo(0));
	}

	@Test
	public void simple() {
		final MorseWord mw = new MorseWord(MorseCharacter.A, MorseCharacter.B, MorseCharacter.C);
		MatcherAssert.assertThat(mw.size(), equalTo(3));
	}

}
