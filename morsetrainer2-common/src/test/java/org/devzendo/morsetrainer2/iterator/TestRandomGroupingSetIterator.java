package org.devzendo.morsetrainer2.iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.matchesPattern;

import java.util.Optional;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestRandomGroupingSetIterator {
	private static final MorseCharacter[] ABC = new MorseCharacter[] { MorseCharacter.A, MorseCharacter.B,
			MorseCharacter.C };
	private static final MorseCharacter[] EMPTY = new MorseCharacter[] {};

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void sourceSetArrayCannotBeNull() throws Exception {
		constructWithBadSourceSetArray(null);
	}

	@Test
	public void sourceSetArrayCannotBeEmpty() throws Exception {
		constructWithBadSourceSetArray(EMPTY);
	}

	private void constructWithBadSourceSetArray(final MorseCharacter[] sourceSetArray) {
		thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Source set array cannot be null or empty");
        new RandomGroupingSetIterator(Optional.of(3), sourceSetArray);
	}
	
	@Test
	public void groupsAreFormedWithEqualLength() {
		final RandomGroupingSetIterator it = new RandomGroupingSetIterator(Optional.of(3), ABC);
		final String out = getString(it);
		assertThat(out.length(), equalTo(((3 + 1) * 25) - 1)); // 99
		assertThat(out, matchesPattern("^([ABC]{3}\\s){24}([ABC]){3}$"));
	}

	@Test
	public void groupsAreFormedWithRandomLength() {
		final RandomGroupingSetIterator it = new RandomGroupingSetIterator(Optional.empty(), ABC);
		final String out = getString(it);
		System.out.println(out);
		assertThat(out.length(), greaterThan(((1 + 1) * 25) - 1)); // 49
		assertThat(out, matchesPattern("^([ABC]{1,9}\\s){24}([ABC]){1,9}$"));
	}

	@Test
	public void testFixedLength() throws Exception {
		final RandomGroupingSetIterator it = new RandomGroupingSetIterator(Optional.of(3), ABC);
		// I miss scalacheck....
		for (int i = 0; i < 10; i++) {
			assertThat(it.generateGroupSize(), equalTo(3));
		}
	}

	@Test
	public void testRandomLengthFlakyTest() throws Exception {
		// NOTE: this test is flaky, depending on randomness....
		final RandomGroupingSetIterator it = new RandomGroupingSetIterator(Optional.empty(), ABC);
		int lowest = 999;
		int highest = -999;
		// I miss scalacheck....
		for (int i = 0; i < 100000; i++) {
			final int size = it.generateGroupSize();
			if (size < lowest) {
				lowest = size;
			}
			if (size > highest) {
				highest = size;
			}
		}
		assertThat(lowest, equalTo(1));
		assertThat(highest, equalTo(9));
	}

	@Test
	public void generateGroupOfGivenSize() throws Exception {
		final RandomGroupingSetIterator it = new RandomGroupingSetIterator(Optional.of(8), ABC);
		final String str = getGeneratedGroupString(it, 8);
		assertThat(str, matchesPattern("^[ABC]{8}$"));
	}

	private String getGeneratedGroupString(final RandomGroupingSetIterator it, final int size) {
		final StringBuilder sb = new StringBuilder();
		for (PartyMorseCharacter pmc : it.generate(size)) {
			sb.append(pmc.getRight().toString());
		}
		return sb.toString();
	}

	private String getString(final RandomGroupingSetIterator it) {
		final StringBuilder sb = new StringBuilder();
		while (it.hasNext()) {
			final PartyMorseCharacter pmc = it.next();
			assertThat(pmc.getLeft(), equalTo(0)); // it's all party 0
			sb.append(pmc.getRight().toString());
		}
		return sb.toString();
	}

}
