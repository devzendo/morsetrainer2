package org.devzendo.morsetrainer2.cmd;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Properties;

import org.devzendo.morsetrainer2.logging.LoggingUnittest;
import org.devzendo.morsetrainer2.source.Source;
import org.devzendo.morsetrainer2.source.Source.SourceType;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestCommandLineParser {
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
		assertThat(options.source, equalTo(Source.SourceType.All));
		assertThat(options.sourceString, equalTo("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,./?+=<AR><AS><BK><BT><CL><CQ><HH><KA><KN><NR><SK><VE>"));
	}

	@Test
	public void lettersSource() throws Exception {
		final Options options = construct("-source", "LeTTeRs").getOptions();
		assertThat(options.source, equalTo(Source.SourceType.Letters));
		assertThat(options.sourceString, equalTo("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
	}

	@Test
	public void numbersSource() throws Exception {
		final Options options = construct("-source", "Numbers").getOptions();
		assertThat(options.source, equalTo(Source.SourceType.Numbers));
		assertThat(options.sourceString, equalTo("0123456789"));
	}

	@Test
	public void punctuationSource() throws Exception {
		final Options options = construct("-source", "punctuation").getOptions();
		assertThat(options.source, equalTo(Source.SourceType.Punctuation));
		assertThat(options.sourceString, equalTo(",./?+="));
	}

	@Test
	public void prosignsSource() throws Exception {
		final Options options = construct("-source", "prosigns").getOptions();
		assertThat(options.source, equalTo(Source.SourceType.Prosigns));
		assertThat(options.sourceString, equalTo("<AR><AS><BK><BT><CL><CQ><HH><KA><KN><NR><SK><VE>"));
	}

	@Test
	public void callsignsSource() throws Exception {
		final Options options = construct("-source", "Callsigns").getOptions();
		assertThat(options.source, equalTo(Source.SourceType.Callsigns));
		// can't test source string, it's random
	}

	@Test
	public void qsoSource() throws Exception {
		final Options options = construct("-source", "QSO").getOptions();
		assertThat(options.source, equalTo(Source.SourceType.QSO));
		// can't test source string, it's random
	}

	@Test
	public void setSource() throws Exception {
		final Options options = construct("-source", "set", "abcz<AR>").getOptions();
		assertThat(options.source, equalTo(Source.SourceType.Set));
		assertThat(options.sourceString, equalTo("ABCZ<AR>"));
	}

	@Test
	public void fileSource() throws Exception {
		final Options options = construct("-source", "file", "src/test/resources/input.txt").getOptions();
		assertThat(options.source, equalTo(Source.SourceType.File));
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
			assertThat(options.source, equalTo(Source.SourceType.Stdin));
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

	@Test
	public void nonexistentRecordFileIsFine() throws Exception {
		final Options options = construct("-record", "target/nonexistent.wav").getOptions();
		assertThat(options.recordFile.isPresent(), equalTo(true));
		assertThat(options.recordFile.get(), equalTo(new File("target/nonexistent.wav")));
	}

	@Test
	public void cannotOverwriteExistentRecordFile() throws Exception {
		final File temp = Files.createTempFile("recording", ".wav").toFile();
		try {
			constructWithFailure("Cannot overwrite existing recording '" + temp.getAbsolutePath() + "'", "-record", temp.getAbsolutePath());
		} finally {
			assertThat(temp.delete(), equalTo(true));
		}
	}

	@Test
	public void emptyRecordFile() throws Exception {
		constructWithFailure("-record <file> must be followed by a file name", "-record");
	}
	
	@Test
	public void randomLength() throws Exception {
		final Options options = construct("-length", "random").getOptions();
		assertThat(options.length.isPresent(), equalTo(false));
	}
	
	@Test
	public void randomLengthByDefault() throws Exception {
		final Options options = construct().getOptions();
		assertThat(options.length.isPresent(), equalTo(false));
	}

	@Test
	public void zeroIsInvalidLength() throws Exception {
		constructWithFailure("-length <1..9|random> must be followed by an number in the range 1 to 9, or 'random'", "-length", "0");
	}

	@Test
	public void tenIsInvalidLength() throws Exception {
		constructWithFailure("-length <1..9|random> must be followed by an number in the range 1 to 9, or 'random'", "-length", "10");
	}

	@Test
	public void nonIntegerIsInvalid() throws Exception {
		constructWithFailure("-length <1..9|random> must be followed by an number in the range 1 to 9, or 'random'", "-length", "nobody expects the Spanish inquisition");
	}

	@Test
	public void emptyLength() throws Exception {
		constructWithFailure("-length <1..9|random> must be followed by an number in the range 1 to 9, or 'random'", "-length");
	}

	@Test
	public void validLengths() throws Exception {
		for (int i=1; i<10; i++) {
			final Options options = construct("-length", "" + i).getOptions();
			assertThat(options.length.isPresent(), equalTo(true));
			assertThat(options.length.get(), equalTo(new Integer(i)));
		}
	}
	
	@Test
	public void interactiveAndRecordNotAllowed() throws Exception {
		constructWithFailure("-interactive cannot be used with -record", "-interactive", "-record", "target/nonexistent.wav");
		constructWithFailure("-interactive cannot be used with -record", "-record", "target/nonexistent.wav", "-interactive");
	}

	@Test
	public void emptyText() throws Exception {
		constructWithFailure("-source text <some text> must be followed by a string to play", "-source", "text");
	}

	@Test
	public void textSource() throws Exception {
		final Options options = construct("-source", "text", "1 hoopy $ frood <kn>").getOptions();
		assertThat(options.source, equalTo(SourceType.Text));
		assertThat(options.sourceString, equalTo("1 HOOPY  FROOD <KN>"));
	}

	@Test
	public void unknownArg() throws Exception {
		constructWithFailure("Unknown option '-fish'", "-fish");
	}
	
	private void constructWithFailure(final String message, final String ... args) {
		thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(message);
        construct(args);
	}

	private CommandLineParser construct(final String ... args) {
		return new CommandLineParser(Arrays.asList(args), properties);
	}
}