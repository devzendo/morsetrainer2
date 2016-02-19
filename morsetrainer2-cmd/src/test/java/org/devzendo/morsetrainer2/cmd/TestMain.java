package org.devzendo.morsetrainer2.cmd;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.devzendo.morsetrainer2.logging.LoggingUnittest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestMain {
	private static Properties properties = Main.getPropertiesResource();
	
	@BeforeClass
	public static void setupLogging() {
		LoggingUnittest.initialise();
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// WPM
	
	@Test
	public void wpmAsLast() {
		constructWithFailure("-wpm must be in the range 12 to 60", "-wpm");
	}

	@Test
	public void wpmWithNonInteger() {
		constructWithFailure("-wpm must be in the range 12 to 60", "-wpm", "zounds");
	}

	@Test
	public void wpmUnderRange() {
		constructWithFailure("-wpm must be in the range 12 to 60", "-wpm", "11");
	}

	@Test
	public void wpmOverRange() {
		constructWithFailure("-wpm must be in the range 12 to 60", "-wpm", "61");
	}

	// FWPM
	
	@Test
	public void fwpmAsLast() {
		constructWithFailure("-fwpm must be in the range 12 to 60", "-fwpm");
	}

	@Test
	public void fwpmWithNonInteger() {
		constructWithFailure("-fwpm must be in the range 12 to 60", "-fwpm", "zounds");
	}

	@Test
	public void fwpmUnderRange() {
		constructWithFailure("-fwpm must be in the range 12 to 60", "-fwpm", "11");
	}

	@Test
	public void fwpmOverRange() {
		constructWithFailure("-fwpm must be in the range 12 to 60", "-fwpm", "61");
	}

	// FREQ
	
	@Test
	public void freqAsLast() {
		constructWithFailure("-freq must be in the range 400 to 800", "-freq");
	}

	@Test
	public void freqWithNonInteger() {
		constructWithFailure("-freq must be in the range 400 to 800", "-freq", "zounds");
	}

	@Test
	public void freqUnderRange() {
		constructWithFailure("-freq must be in the range 400 to 800", "-freq", "399");
	}

	@Test
	public void freqOverRange() {
		constructWithFailure("-freq must be in the range 400 to 800", "-freq", "801");
	}

	// full options
	@Test
	public void fwpmSameAsWpmIfNotGiven() throws Exception {
		final Options options = construct("-wpm", "17").getOptions();
		assertThat(options.wpm, equalTo(new Integer("17")));
		assertThat(options.fwpm, equalTo(new Integer("17")));
	}

	@Test
	public void freqDefaultedInIfNotGiven() throws Exception {
		final Options options = construct("-wpm", "17").getOptions();
		assertThat(options.freqHz, equalTo(new Integer("600")));
	}

	@Test
	public void wpmAndFwpmAndFreqDefaultedInIfNotGiven() throws Exception {
		final Options options = construct().getOptions();
		assertThat(options.wpm, equalTo(new Integer("12")));
		assertThat(options.fwpm, equalTo(new Integer("12")));
		assertThat(options.freqHz, equalTo(new Integer("600")));
	}

	@Test
	public void fwpmCanBeVaried() throws Exception {
		final Options options = construct("-fwpm", "17").getOptions();
		assertThat(options.wpm, equalTo(new Integer("12")));
		assertThat(options.fwpm, equalTo(new Integer("17")));
	}
	
	// Source
	
	@Test
	public void defaultSource() throws Exception {
		final Options options = construct().getOptions();
		assertThat(options.source, equalTo(Options.Source.All));
		assertThat(options.sourceString, equalTo("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,./?+=<AR><AS><BK><BT><CL><CQ><HH><KA><KN><NR><SK><VE>"));
	}

	@Test
	public void lettersSource() throws Exception {
		final Options options = construct("-source", "LeTTeRs").getOptions();
		assertThat(options.source, equalTo(Options.Source.Letters));
		assertThat(options.sourceString, equalTo("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
	}

	@Test
	public void numbersSource() throws Exception {
		final Options options = construct("-source", "Numbers").getOptions();
		assertThat(options.source, equalTo(Options.Source.Numbers));
		assertThat(options.sourceString, equalTo("0123456789"));
	}

	@Test
	public void punctuationSource() throws Exception {
		final Options options = construct("-source", "punctuation").getOptions();
		assertThat(options.source, equalTo(Options.Source.Punctuation));
		assertThat(options.sourceString, equalTo(",./?+="));
	}

	@Test
	public void prosignsSource() throws Exception {
		final Options options = construct("-source", "prosigns").getOptions();
		assertThat(options.source, equalTo(Options.Source.Prosigns));
		assertThat(options.sourceString, equalTo("<AR><AS><BK><BT><CL><CQ><HH><KA><KN><NR><SK><VE>"));
	}

	@Test
	public void callsignsSource() throws Exception {
		final Options options = construct("-source", "Callsigns").getOptions();
		assertThat(options.source, equalTo(Options.Source.Callsigns));
		// can't test source string, it's random
	}

	@Test
	public void qsoSource() throws Exception {
		final Options options = construct("-source", "QSO").getOptions();
		assertThat(options.source, equalTo(Options.Source.QSO));
		// can't test source string, it's random
	}

	@Test
	public void setSource() throws Exception {
		final Options options = construct("-source", "set", "abcz<AR>").getOptions();
		assertThat(options.source, equalTo(Options.Source.Set));
		assertThat(options.sourceString, equalTo("ABCZ<AR>"));
	}

	@Test
	public void fileSource() throws Exception {
		final Options options = construct("-source", "file", "src/test/resources/input.txt").getOptions();
		assertThat(options.source, equalTo(Options.Source.File));
		assertThat(options.sourceString, equalTo("ABCDE"));
	}

	@Test
	public void emptyFileSource() throws Exception {
		constructWithFailure("-source file must be followed by a file name", "-source", "file");
	}

	@Test
	public void nonexistentFileSource() throws Exception {
		thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(matchesPattern("File '.*' not found"));
		construct("-source", "file", "nonexistent");
	}

	@Test
	public void stdinHyphenSource() throws Exception {
		tryStdinSource("-");
	}

	@Test
	public void stdinStdinSource() throws Exception {
		tryStdinSource("STDIN");
	}

	@Test
	public void stdinStdinCaseSource() throws Exception {
		tryStdinSource("sTdIN");
	}

	private void tryStdinSource(final String sourceName) throws IOException {
		final InputStream origStdin = System.in;
		try(final ByteArrayInputStream bais = new ByteArrayInputStream("STDINTXT".getBytes())) {
			System.setIn(bais);

			final Options options = construct("-source", sourceName).getOptions();
			assertThat(options.source, equalTo(Options.Source.Stdin));
			assertThat(options.sourceString, equalTo("STDINTXT"));
			
		} finally {
			System.setIn(origStdin);
		}
	}

	@Test
	public void unknownSource() throws Exception {
		constructWithFailure("-source must be followed by a source type [all|letters|numbers|punctuation|prosigns|qso]", "-source", "zarjaz");
	}

	@Test
	public void emptySource() throws Exception {
		constructWithFailure("-source must be followed by a source type [all|letters|numbers|punctuation|prosigns|qso]", "-source");
	}

	@Test
	public void emptySetSource() throws Exception {
		constructWithFailure("-source set must be followed by a string of source characters", "-source", "set");
	}

	@Test
	public void defaultNonInteractive() throws Exception {
		assertThat(construct().getOptions().interactive, equalTo(false));
	}

	@Test
	public void interactive() throws Exception {
		assertThat(construct("-interactive").getOptions().interactive, equalTo(true));
	}

	private void constructWithFailure(final String message, final String ... args) {
		thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(message);
        construct(args);
	}

	private Main construct(final String ... args) {
		return new Main(Arrays.asList(args), properties);
	}
}
