package org.devzendo.morsetrainer2.cmd;

import static java.util.Collections.singleton;
import static org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser.parseToSet;
import static org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser.parseToWord;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

import org.devzendo.morsetrainer2.logging.LoggingUnittest;
import org.devzendo.morsetrainer2.source.Source;
import org.devzendo.morsetrainer2.source.Source.PlayType;
import org.devzendo.morsetrainer2.source.Source.SourceType;
import org.devzendo.morsetrainer2.symbol.MorseWord;
import org.hamcrest.Matchers;
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
	public void defaultSourceIsAllWithNoPlay() throws Exception {
		final Options options = construct().getOptions();
		assertThat(options.source, equalTo(singleton(Source.SourceType.All)));
		assertThat(options.sourceChars, equalTo(parseToSet("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,./?+=<AR><AS><BK><BT><CL><CQ><HH><KA><KN><NR><SK><VE>")));
		assertThat(options.play, equalTo(Optional.empty()));
	}

	@Test
	public void lettersSource() throws Exception {
		final Options options = construct("-source", "LeTTeRs").getOptions();
		assertThat(options.source, equalTo(singleton(Source.SourceType.Letters)));
		assertThat(options.sourceChars, equalTo(parseToSet("ABCDEFGHIJKLMNOPQRSTUVWXYZ")));
	}

	@Test
	public void numbersSource() throws Exception {
		final Options options = construct("-source", "Numbers").getOptions();
		assertThat(options.source, equalTo(singleton(Source.SourceType.Numbers)));
		assertThat(options.sourceChars, equalTo(parseToSet("0123456789")));
	}

	@Test
	public void punctuationSource() throws Exception {
		final Options options = construct("-source", "punctuation").getOptions();
		assertThat(options.source, equalTo(singleton(Source.SourceType.Punctuation)));
		assertThat(options.sourceChars, equalTo(parseToSet(",./?+=")));
	}

	@Test
	public void prosignsSource() throws Exception {
		final Options options = construct("-source", "prosigns").getOptions();
		assertThat(options.source, equalTo(singleton(Source.SourceType.Prosigns)));
		assertThat(options.sourceChars, equalTo(parseToSet("<AR><AS><BK><BT><CL><CQ><HH><KA><KN><NR><SK><VE>")));
	}

	@Test
	public void setSource() throws Exception {
		final Options options = construct("-source", "set", "abcz<AR>").getOptions();
		assertThat(options.source, equalTo(singleton(Source.SourceType.Set)));
		assertThat(options.sourceChars, equalTo(parseToSet("ABCZ<AR>")));
	}

	@Test
	public void multipleSource() throws Exception {
		final Options options = construct("-source", "set", "abcz<AR>", "-source", "numbers", "-source", "punctuation").getOptions();
		assertThat(options.source, Matchers.containsInAnyOrder(Source.SourceType.Set, Source.SourceType.Numbers, Source.SourceType.Punctuation));
		assertThat(options.sourceChars, equalTo(parseToSet("1234567890,./?+=ABCZ<AR>")));
	}

	@Test
	public void callsignsSource() throws Exception {
		final Options options = construct("-source", "Callsigns").getOptions();
		assertThat(options.source, equalTo(singleton(Source.SourceType.Callsigns)));
		assertThat(options.sourceChars, empty());
		assertThat(options.sourceWords, empty());
	}

	@Test
	public void qsoSource() throws Exception {
		final Options options = construct("-source", "QSO").getOptions();
		assertThat(options.source, equalTo(singleton(Source.SourceType.QSO)));
		assertThat(options.sourceChars, empty());
		assertThat(options.sourceWords, empty());
	}

	@Test
	public void filePlay() throws Exception {
		final Options options = construct("-play", "file", "src/test/resources/input.txt").getOptions();
		assertThat(options.play, equalTo(Optional.of(Source.PlayType.File)));
		assertThat(options.playString, equalTo("ABCDE ")); // Note space added at end of line.
	}

	@Test
	public void emptyFilePlay() throws Exception {
		constructWithFailure("-play file must be followed by a file name", "-play", "file");
	}

	@Test
	public void nonexistentFilePlay() throws Exception {
		thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(matchesPattern("File '.*' not found"));
		construct("-play", "file", "nonexistent");
	}

	@Test
	public void stdinHyphenPlay() throws Exception {
		tryStdinPlay("-");
	}

	@Test
	public void stdinStdinPlay() throws Exception {
		tryStdinPlay("STDIN");
	}

	@Test
	public void stdinStdinCasePlay() throws Exception {
		tryStdinPlay("sTdIN");
	}

	private void tryStdinPlay(final String playName) throws IOException {
		final InputStream origStdin = System.in;
		try(final ByteArrayInputStream bais = new ByteArrayInputStream("STDINTXT\r\n INPUT".getBytes())) {
			System.setIn(bais);

			final Options options = construct("-play", playName).getOptions();
			assertThat(options.play, equalTo(Optional.of(Source.PlayType.Stdin)));
			assertThat(options.playString, equalTo("STDINTXT  INPUT ")); // Note spaces

		} finally {
			System.setIn(origStdin);
		}
	}

	@Test
	public void emptySetSource() throws Exception {
		constructWithFailure("-source set must be followed by a string of source characters", "-source", "set");
	}

	@Test
	public void unknownSource() throws Exception {
		constructWithFailure("-source must be followed by a source type [all|letters|numbers|punctuation|prosigns|set|codes|words|callsigns|qso]", "-source", "zarjaz");
	}

	@Test
	public void emptySource() throws Exception {
		constructWithFailure("-source must be followed by a source type [all|letters|numbers|punctuation|prosigns|set|codes|words|callsigns|qso]", "-source");
	}

	@Test
	public void unknownPlay() throws Exception {
		constructWithFailure("-play must be followed by an input type [file|stdin|-]", "-play", "zarjaz");
	}

	@Test
	public void emptyPlay() throws Exception {
		constructWithFailure("-play must be followed by an input type [file|stdin|-]", "-play");
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
		constructWithFailure("-record <file> must be followed by a file name and optional contents file name", "-record");
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
	public void groupSizeDefault() throws Exception {
		final Options options = construct().getOptions();
		assertThat(options.groupSize, equalTo(25));
	}

	@Test
	public void groupSizeOutOfRangeOrIllegal() throws Exception {
		constructWithFailure("-groupsize must be in the range 10 to 250", "-groupsize", "9");
		constructWithFailure("-groupsize must be in the range 10 to 250", "-groupsize", "251");
		constructWithFailure("-groupsize must be in the range 10 to 250", "-groupsize", "fish");
		constructWithFailure("-groupsize must be in the range 10 to 250", "-groupsize");
	}

	@Test
	public void groupSizeCanBeSet() throws Exception {
		final Options options = construct("-groupsize", "30").getOptions();
		assertThat(options.groupSize, equalTo(30));
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
	public void emptyTextPlay() throws Exception {
		constructWithFailure("-play text <some text> must be followed by a string to play", "-play", "text");
	}

	@Test
	public void cannotUseBothPlayAndSource() throws Exception {
		constructWithFailure("-play ... and -source ... cannot be used together", "-play", "text", "foo", "-source", "numbers");
	}

	@Test
	public void playDoesNotSetSource() throws Exception {
		final Options options = construct("-play", "text", "1 hoopy $ frood <kn>").getOptions();
		assertThat(options.source, empty());
		assertThat(options.play, equalTo(Optional.of(PlayType.Text)));
	}

	@Test
	public void sourceDoesNotSetPlay() throws Exception {
		final Options options = construct("-source", "all").getOptions();
		assertThat(options.play, equalTo(Optional.empty()));
		assertThat(options.source, equalTo(singleton(SourceType.All)));
	}

	@Test
	public void textPlay() throws Exception {
		final Options options = construct("-play", "text", "1 hoopy $ frood <kn>").getOptions();
		assertThat(options.source, empty());
		assertThat(options.play, equalTo(Optional.of(PlayType.Text)));
		assertThat(options.playString, equalTo("1 HOOPY  FROOD <KN>"));
	}

	@Test
	public void codesSource() throws Exception {
		final Options options = construct("-source", "codes").getOptions();
		assertThat(options.source, equalTo(singleton(SourceType.Codes)));
		assertThat(options.sourceChars, empty());
		assertThat(options.sourceWords, not(empty()));
		assertThat(options.play, equalTo(Optional.empty()));
		assertThat(options.playString, equalTo(""));
	}

	@Test
	public void wordsSource() throws Exception {
		final Options options = construct("-source", "words").getOptions();
		assertThat(options.source, equalTo(singleton(SourceType.Words)));
		assertThat(options.sourceChars, empty());
		assertThat(options.sourceWords, not(empty()));
		assertThat(options.play, equalTo(Optional.empty()));
		assertThat(options.playString, equalTo(""));
	}

	@Test
	public void codesSourceAndCharactersCannotBeUsedTogether() throws Exception {
		constructWithFailure("Cannot mix random word and character generators", "-source", "codes", "-source", "letters");
	}

	@Test
	public void callsignsSourceAndCharactersCannotBeUsedTogether() throws Exception {
		constructWithFailure("Cannot mix random word and character generators", "-source", "callsigns", "-source", "letters");
	}

	@Test
	public void qsoSourceAndCharactersCannotBeUsedTogether() throws Exception {
		constructWithFailure("Cannot mix random word and character generators", "-source", "qso", "-source", "letters");
	}

	@Test
	public void generatorsAndWordSourcesCannotBeUsedTogether() throws Exception {
		constructWithFailure("Cannot mix word generators with callsigns or QSO", "-source", "qso", "-source", "codes", "-source", "callsigns");
	}

	@Test
	public void multipleWordGeneratorsCanBeUsedTogether() throws Exception {
		final Options options = construct("-source", "words", "-source", "codes").getOptions();
		assertThat(options.source, containsInAnyOrder(SourceType.Words, SourceType.Codes));
		assertThat(options.sourceChars, empty());
		assertThat(options.sourceWords, not(empty()));
		final MorseWord wordFromCodes = parseToWord("BURO");
		final MorseWord wordFromWords = parseToWord("BECAUSE");
		assertThat(options.sourceWords, hasItems(wordFromCodes, wordFromWords));
		assertThat(options.play, equalTo(Optional.empty()));
		assertThat(options.playString, equalTo(""));
	}

	@Test
	public void unknownArg() throws Exception {
		constructWithFailure("Unknown option '-fish'", "-fish");
	}

	private void constructWithFailure(final String message, final String ... args) {
		thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo(message));
        construct(args);
	}

	private CommandLineParser construct(final String ... args) {
		return new CommandLineParser(Arrays.asList(args), properties);
	}
}
