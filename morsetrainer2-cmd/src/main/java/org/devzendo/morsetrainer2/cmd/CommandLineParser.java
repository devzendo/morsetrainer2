package org.devzendo.morsetrainer2.cmd;

import static org.devzendo.morsetrainer2.cmd.AnsiHelper.println;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.devzendo.morsetrainer2.source.Source;
import org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineParser.class);
	private int cmdIndex = 0;
	private final List<String> finalArgList;
	private final Options options = new Options();

	public CommandLineParser(final List<String> finalArgList, final Properties properties) {
		this.finalArgList = finalArgList;

		while (hasNextArg()) {
			final String arg = nextArg();

			switch (arg) {
			case "-version":
				showVersion(properties);
				finish();
			case "-usage":
			case "-?":
			case "-help":
				usage();
				finish();
			case "-wpm":
				options.wpm = nextNumArg(12, 60, "wpm");
				break;
			case "-fwpm":
				options.fwpm = nextNumArg(12, 60, "fwpm");
				break;
			case "-freq":
				options.freqHz = nextNumArg(400, 800, "freq");
				break;
			case "-source":
				options.source = nextSourceArg();
				switch (options.source) {
				case All:
				case Letters:
				case Numbers:
				case Prosigns:
				case Punctuation:
					options.sourceString = options.source.content();
					break;
				case Set:
					if (hasNextArg()) {
						final String setString = nextArg();
						options.sourceString = TextToMorseCharacterParser.parseToString(setString);
					} else {
						throw new IllegalArgumentException(
								"-source set must be followed by a string of source characters");
					}
					break;
				case File:
					if (hasNextArg()) {
						options.sourceString = TextToMorseCharacterParser.parseToString(readFile(new File(nextArg())));
					} else {
						throw new IllegalArgumentException("-source file must be followed by a file name");
					}
					break;
				case Stdin:
					options.sourceString = TextToMorseCharacterParser.parseToString(readStdin());
					break;
				case Text:
					if (hasNextArg()) {
						final String textString = nextArg();
						options.sourceString = TextToMorseCharacterParser.parseToString(textString);
					} else {
						throw new IllegalArgumentException(
								"-source text <some text> must be followed by a string to play");
					}
					break;
				// No source string is set for these....
				case QSO:
				case Callsigns:
					options.sourceString = "";
					break;
				}
				break;
			case "-interactive":
				options.interactive = true;
				break;
			case "-record":
				if (hasNextArg()) {
					final File recordingFile = new File(nextArg());
					if (recordingFile.exists()) {
						throw new IllegalArgumentException(
								"Cannot overwrite existing recording '" + recordingFile.getAbsolutePath() + "'");
					}
					options.recordFile = Optional.of(recordingFile);
				} else {
					throw new IllegalArgumentException("-record <file> must be followed by a file name");
				}
				break;
			case "-length":
				boolean badArg = true;
				if (hasNextArg()) {
					final String lengthString = nextArg().trim();
					if (lengthString.toLowerCase().equals("random")) {
						badArg = false;
						options.length = Optional.empty();
					} else {
						try {
							final int parsedInt = Integer.parseInt(lengthString);
							if (parsedInt >= 1 && parsedInt <= 9) {
								badArg = false;
								options.length = Optional.of(parsedInt);
							}
						} catch (final NumberFormatException nfe) {
							// ok, just let badArg be true and fall thru...
						}
					}
				}
				if (badArg) {
					throw new IllegalArgumentException(
							"-length <1..9|random> must be followed by an number in the range 1 to 9, or 'random'");
				}
				break;
			default:
				usage();
				throw new IllegalArgumentException("Unknown option '" + arg + "'");
			}
		}
		// Fill in defaults
		if (options.wpm == null) {
			options.wpm = 12;
		}
		if (options.fwpm == null) {
			options.fwpm = options.wpm;
		}
		if (options.freqHz == null) {
			options.freqHz = 600;
		}
		// Final validation
		if (options.interactive && options.recordFile.isPresent()) {
			throw new IllegalArgumentException("-interactive cannot be used with -record");
		}
	}

	private void finish() {
		System.exit(0);
	}

	public Options getOptions() {
		return options;
	}

	private boolean hasNextArg() {
		return cmdIndex < finalArgList.size();
	}

	private String nextArg() {
		return finalArgList.get(cmdIndex++);
	}

	private Integer nextNumArg(final int low, final int high, final String name) {
		if (hasNextArg()) {
			final String nextArg = nextArg();
			try {
				final Integer out = Integer.parseInt(nextArg);
				if (out < low || out > high) {
					LOGGER.error("The " + name + " '" + nextArg + "' is not in the range " + low + " to " + high);
				} else {
					return out;
				}
			} catch (final NumberFormatException nfe) {
				LOGGER.error("The " + name + " '" + nextArg + "' is not an integer");
			}
		}
		throw new IllegalArgumentException("-" + name + " must be in the range " + low + " to " + high);
	}

	private Source.SourceType nextSourceArg() {
		if (hasNextArg()) {
			final Optional<Source.SourceType> out = Source.SourceType.fromString(nextArg());
			if (out.isPresent()) {
				return out.get();
			}
		}
		throw new IllegalArgumentException(
				"-source must be followed by a source type [all|letters|numbers|punctuation|prosigns|qso]");
	}

	private String readFile(final File file) {
		try {
			return readInputStream(new FileInputStream(file));
		} catch (final FileNotFoundException e) {
			final String msg = "File '" + file.getAbsolutePath() + "' not found";
			LOGGER.error(msg);
			throw new IllegalArgumentException(msg, e);
		}
	}

	private String readInputStream(final InputStream in) {
		try {
			final StringBuilder sb = new StringBuilder();
			final BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append(' '); // newlines are spaces, otherwise words at end of lines may run into each other.
			}
			return sb.toString();
		} catch (final IOException ioe) {
			final String msg = "Failure reading input: " + ioe.getMessage();
			LOGGER.error(msg);
			throw new RuntimeException(msg, ioe);
		} finally {
			try {
				in.close();
			} catch (final IOException ioe) {
				final String msg = "Failure closing input: " + ioe.getMessage();
				LOGGER.error(msg);
				throw new RuntimeException(msg, ioe);
			}
		}
	}

	private String readStdin() {
		return readInputStream(System.in);
	}

	private void showVersion(final Properties properties) {
		final String version = properties.getProperty("version");
		println("MorseTrainer2 version @|green " +  version + " |@");
		println("Copyleft 2016 Matt Gumbley M0CUV, all rights reversed.");
		println("Distributed under the @|yellow Apache License, Version 2|@");
	}

	private void usage() {
		//           01234567890123456789012345678901234567890123456789012345678901234567890123456789
		println("@|bold mt2 [options] [source] |@");
		println("@|bold Options: |@");
		println("@|yellow -wpm <words per min>|@  - Set the speed in words per minute.");
		println("                        Default is 12 WPM if not given.");
		println("                        Must be between 12 and 60.");
		println("@|yellow -fwpm <words per min>|@ - Set the Farnsworth speed in words per minute.");
		println("                        Default matches the WPM if not given.");
		println("@|yellow -freq <Hz>|@            - Set the tone frequency in Hertz.");
		println("                        Default is 600 Hz if not given.");
		println("@|yellow -interactive|@          - Interactively query for what you heard, to test/assess");
		println("                        your recognition progress. If not given, just plays/");
		println("                        or records.");
		println("@|yellow -record <filename>|@    - Records the Morse to a .wav file. Can't be used with");
		println("                        -interactive mode. If not given, just plays to speakers.");
		println("@|yellow -length <1..9|random>|@ - Fixed or random length of sent character groups.");
		println("                        Default is random (up to 9) if not given.");
		println("");
		println("@|bold Source (set of characters to randomise, and train with):|@");
		println("@|cyan -source [all|letters|numbers|punctuation|prosigns|set]|@");
		println("@|cyan -source set '....'|@");
		println("                      - Use letters, numbers etc. as the characters");
		println("                        to send, or with set '...', use the specific");
		println("                        characters specified.");
		println("                        Default is 'all' if not given.");
		println("@|bold Source (text to play/generate):|@");
		println("@|green -source [stdin|-|file|callsigns|qso|text]|@");
		println("@|green -source stdin   or  -source -|@");
		println("                      - Play the text from standard input.");
		println("@|green -source file <filename>|@");
		println("                      - Play the text read from a file.");
		println("@|green -source text <some text>|@");
		println("                      - Play the text in the next argument (beware quoting).");
		println("");
		println("@|bold -? or -help or -usage|@ - Show this usage summary");
		println("@|bold -version|@              - Show MorseTrainer2 version number");
		println("");
		println("@|bold Log4j output control options:|@");
		println("-debug                - Set the log level to debug (default is info).");
		println("-warn                 - Set the log level to warning.");
		println("-level                - Show log levels of each log line output.");
		println("-classes              - Show class names in each log line output.");
		println("-threads              - Show thread names in each log line output.");
		println("-times                - Show timing data in each log line output.");
	}
}
