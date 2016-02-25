package org.devzendo.morsetrainer2.iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.devzendo.morsetrainer2.symbol.PartyMorseCharacter;
import org.junit.Test;

public class TestVerbatimIterator {

	@Test
	public void emptinessOfNull() {
		assertThat(extractToString(new VerbatimIterator(null)), equalTo(""));
	}

	@Test
	public void emptinessOfEmpty() {
		assertThat(extractToString(new VerbatimIterator("")), equalTo(""));
	}

	@Test
	public void verbatimTextFilteredByMorseCharacter() {
		assertThat(extractToString(new VerbatimIterator(" Abc 3  $ <Kn><zq> D.")), equalTo(" ABC 3   <KN> D."));
	}

	@Test
	public void spacesThenLineEndingsCoalescedToSingleSpace() {
		assertThat(extractToString(new VerbatimIterator("a\nb")), equalTo("A B"));
		assertThat(extractToString(new VerbatimIterator(" a\n ")), equalTo(" A"));
		assertThat(extractToString(new VerbatimIterator(" a\np")), equalTo(" A P"));
		assertThat(extractToString(new VerbatimIterator(" a\n p")), equalTo(" A  P"));
		assertThat(extractToString(new VerbatimIterator(" a \n p")), equalTo(" A  P"));
		assertThat(extractToString(new VerbatimIterator(" a  \n p")), equalTo(" A  P"));
		assertThat(extractToString(new VerbatimIterator(" a   \n p")), equalTo(" A  P"));
		assertThat(extractToString(new VerbatimIterator(" a   \nz")), equalTo(" A Z"));
		assertThat(extractToString(new VerbatimIterator(" a   \nz  a   \np")), equalTo(" A Z  A P"));
		assertThat(extractToString(new VerbatimIterator("a\n")), equalTo("A"));
	}

	private String extractToString(final VerbatimIterator verbatimIterator) {
		final StringBuilder sb = new StringBuilder();
		while (verbatimIterator.hasNext()) {
			final PartyMorseCharacter next = verbatimIterator.next();
			assertThat(next.getLeft(), equalTo(0)); // all party 0 for now
			sb.append(next.getRight().toString());
		}
		return sb.toString();
	}
}
