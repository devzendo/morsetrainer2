package org.devzendo.morsetrainer2.editmatcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.devzendo.morsetrainer2.editmatcher.Edit.deletion;
import static org.devzendo.morsetrainer2.editmatcher.Edit.match;
import static org.devzendo.morsetrainer2.editmatcher.Edit.mutation;

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
        assertThat(new EditMatcher("", "").edits(), empty());
    }

    @Test
    public void perfect() {
        assertThat(new EditMatcher("abcd", "abcd").edits(), contains(match('a'), match('b'), match('c'), match('d')));
    }

    @Test
    public void hatTape() {
        assertThat(new EditMatcher("tape", "hat").edits(), contains(mutation('t'), match('a'), deletion('p'), mutation('e')));
    }

    @Test
    public void deletion1() {
        assertThat(new EditMatcher("abcd", "bcd").edits(), contains(deletion('a'), match('b'), match('c'), match('d')));
    }

    @Test
    public void deletion2() {
        assertThat(new EditMatcher("abcd", "aabcd").edits(), contains(deletion('a'), match('a'), match('b'), match('c'), match('d')));
    }

    @Test
    public void deletionInside() {
        assertThat(new EditMatcher("abcd", "ad").edits(), contains(match('a'), deletion('b'), deletion('c'), match('d')));
    }

    @Test
    public void mutation1() {
        assertThat(new EditMatcher("abcd", "nbcd").edits(), contains(mutation('a'), match('b'), match('c'), match('d')));
    }

    @Test
    public void deletionsAtEnd() {
        assertThat(new EditMatcher("abcd", "abcdefg").edits(), contains(
            match('a'), match('b'), match('c'), match('d'),
            deletion('e'), deletion('f'), deletion('g')
        ));
    }

    @Test
    public void deletionsAtEnd2() {
        assertThat(new EditMatcher("abcdefg", "abcd").edits(), contains(
            match('a'), match('b'), match('c'), match('d'),
            deletion('e'), deletion('f'), deletion('g')
        ));
    }
}
