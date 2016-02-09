package org.devzendo.morsetrainer2.xlat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.Arrays;
import java.util.List;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.Pulse;
import org.junit.Test;

public class TestMorseCharactersToPulses {

	final MorseCharactersToPulses xlat = new MorseCharactersToPulses();

	@Test
	public void empty() {
		final List<MorseCharacter> in = Arrays.asList(new MorseCharacter[] {});
		assertThat(xlat.translate(in), hasSize(0));
	}

	@Test
	public void translationOfChar() {
		final List<MorseCharacter> in = Arrays.asList(
				new MorseCharacter[] { MorseCharacter.A });
		final Pulse[] array = xlat.translate(in).toArray(new Pulse[0]);
		assertThat(array, equalTo(new Pulse[] {
				Pulse.dit, // A
				Pulse.el,
				Pulse.dah
		}));
	}

	@Test
	public void translationOfSpace() {
		final List<MorseCharacter> in = Arrays.asList(
				new MorseCharacter[] { MorseCharacter.SPC });
		final Pulse[] array = xlat.translate(in).toArray(new Pulse[0]);
		assertThat(array, equalTo(new Pulse[] {
				Pulse.wo,
		}));
	}

	@Test
	public void translationOfSpaceChar() {
		final List<MorseCharacter> in = Arrays.asList(
				new MorseCharacter[] { MorseCharacter.SPC, MorseCharacter.A });
		final Pulse[] array = xlat.translate(in).toArray(new Pulse[0]);
		assertThat(array, equalTo(new Pulse[] {
				Pulse.wo,
				
				Pulse.dit, // A
				Pulse.el,
				Pulse.dah,
		}));
	}

	@Test
	public void translationOfString() {
		final List<MorseCharacter> in = Arrays.asList(
				new MorseCharacter[] { MorseCharacter.A, MorseCharacter.N, MorseCharacter.SPC, MorseCharacter.Z });
		final Pulse[] array = xlat.translate(in).toArray(new Pulse[0]);
		assertThat(array, equalTo(new Pulse[] {
				Pulse.dit, // A
				Pulse.el,
				Pulse.dah,
				
				Pulse.ch, 
				
				Pulse.dah, // N
				Pulse.el,
				Pulse.dit,
				
				Pulse.wo,
				
				Pulse.dah, // Z
				Pulse.el,
				Pulse.dah,
				Pulse.el,
				Pulse.dit,
				Pulse.el,
				Pulse.dit
		}));
	}
}
