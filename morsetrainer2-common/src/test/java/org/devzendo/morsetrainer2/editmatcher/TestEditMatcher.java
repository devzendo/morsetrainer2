package org.devzendo.morsetrainer2.editmatcher;

import static org.devzendo.morsetrainer2.editmatcher.Edit.deletion;
import static org.devzendo.morsetrainer2.editmatcher.Edit.match;
import static org.devzendo.morsetrainer2.editmatcher.Edit.mutation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

import java.util.List;

import org.devzendo.morsetrainer2.logging.LoggingUnittest;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestEditMatcher {

	@BeforeClass
	public static void setupLogging() {
		LoggingUnittest.initialise();
	}

    @Test
    public void emptyWord() {
        assertThat(matching("", ""), empty());
    }

    @Test
    public void perfect() {
        assertThat(matching("abcd", "abcd"), contains(match('a'), match('b'), match('c'), match('d')));
    }

    @Test
    public void hatTape() {
        assertThat(matching("tape", "hat"), contains(mutation('t'), match('a'), deletion('p'), mutation('e')));
    }

    @Test
    public void deletion1() {
        assertThat(matching("abcd", "bcd"), contains(deletion('a'), match('b'), match('c'), match('d')));
    }

    @Test
    public void deletion2() {
        assertThat(matching("abcd", "aabcd"), contains(deletion('a'), match('a'), match('b'), match('c'), match('d')));
    }

    @Test
    public void deletionInside() {
        assertThat(matching("abcd", "ad"), contains(match('a'), deletion('b'), deletion('c'), match('d')));
    }

    @Test
    public void mutation1() {
        assertThat(matching("abcd", "nbcd"), contains(mutation('a'), match('b'), match('c'), match('d')));
    }

    @Test
    public void deletionsAtEnd() {
        assertThat(matching("abcd","abcdefg"), contains(
            match('a'), match('b'), match('c'), match('d'),
            deletion('e'), deletion('f'), deletion('g')
        ));
    }

    @Test
    public void deletionsAtEnd2() {
        assertThat(matching("abcdefg", "abcd"), contains(
            match('a'), match('b'), match('c'), match('d'),
            deletion('e'), deletion('f'), deletion('g')
        ));
    }

    private List<Edit<Character>> matching(final String s1, final String s2) {
    	return new EditMatcher<Character>(toCA(s1), toCA(s2)).edits();
    }

    private Character[] toCA(final String s) {
    	final Character[] chs = new Character[s.length()];
    	for (int i = 0; i < s.length(); i++) {
    		chs[i] = s.charAt(i);
    	}
    	return chs;
    }
}
