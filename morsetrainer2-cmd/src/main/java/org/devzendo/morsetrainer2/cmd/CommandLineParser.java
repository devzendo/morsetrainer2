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
import org.devzendo.morsetrainer2.source.Source.SourceType;
import org.devzendo.morsetrainer2.symbol.MorseWordResourceLoader;
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
				final SourceType sourceType = nextSourceArg();
				options.source.add(sourceType);
				switch (sourceType) {
					case Set:
						if (hasNextArg()) {
							final String setString = nextArg();
							options.sourceChars.addAll(TextToMorseCharacterParser.parseToSet(setString));
						} else {
							throw new IllegalArgumentException(
									"-source set must be followed by a string of source characters");
						}
						break;
					case Codes:
						options.sourceWords.addAll(MorseWordResourceLoader.wordsFromResource("codes.txt"));
						break;
					case Words:
						options.sourceWords.addAll(MorseWordResourceLoader.wordsFromResource("words.txt"));
						break;
					default:
				}
				break;

			case "-play":
				options.play = nextPlayArg();
				switch (options.play.get()) {
				case File:
					if (hasNextArg()) {
						options.playString = TextToMorseCharacterParser.parseToString(readFile(new File(nextArg())));
					} else {
						throw new IllegalArgumentException("-play file must be followed by a file name");
					}
					break;
				case Stdin:
					options.playString = TextToMorseCharacterParser.parseToString(readStdin());
					break;
				case Text:
					if (hasNextArg()) {
						final String textString = nextArg();
						options.playString = TextToMorseCharacterParser.parseToString(textString);
					} else {
						throw new IllegalArgumentException(
								"-play text <some text> must be followed by a string to play");
					}
					break;
				default:
					throw new IllegalArgumentException("-play must be followed by an input type [file|stdin|-]");
				}
				break;

			case "-interactive":
				options.interactive = true;
				break;

			case "-record":
				if (hasNextArg()) {
					final String fileName = nextArg();
					if (! (fileName.endsWith(".mp3") || fileName.endsWith(".wav"))) {
						throw new IllegalArgumentException("Recording files can only be .wav or .mp3 files");
					}
					final File recordingFile = new File(fileName);
					if (recordingFile.exists()) {
						throw new IllegalArgumentException(
								"Cannot overwrite existing recording '" + recordingFile.getAbsolutePath() + "'");
					}
					options.recordFile = Optional.of(recordingFile);
				} else {
					throw new IllegalArgumentException("-record <file> must be followed by a file name");
				}
				break;

			case "-contents":
				if (hasNextArg()) {
					options.contentsFile = Optional.of(new File(nextArg()));
				} else {
					throw new IllegalArgumentException("-contents <file> must be followed by a file name");
				}
				break;

			case "-length": {
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
			}

			case "-groupsize":
				options.groupSize = nextNumArg(10, 250, "groupsize");
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
		if (!options.source.isEmpty() && options.play.isPresent()) {
			throw new IllegalArgumentException("-play ... and -source ... cannot be used together");
		}
		if (options.source.isEmpty() && !options.play.isPresent()) {
			options.source.add(SourceType.All);
		}
		if (!options.source.isEmpty() && !options.play.isPresent()) {
			options.source.forEach(s -> options.sourceChars.addAll(TextToMorseCharacterParser.parseToSet(s.content())));
		}

		if (options.interactive && options.recordFile.isPresent()) {
			throw new IllegalArgumentException("-interactive cannot be used with -record");
		}

		if (!options.recordFile.isPresent() && options.contentsFile.isPresent()) {
			throw new IllegalArgumentException("-contents only supported if also using -record");
		}

		if (!options.sourceChars.isEmpty() && (
				options.source.contains(SourceType.Codes) || options.source.contains(SourceType.QSO) ||
				options.source.contains(SourceType.Callsigns)
				)) {
			throw new IllegalArgumentException("Cannot mix random word and character generators");
		}

		if ((options.source.contains(SourceType.Codes) || options.source.contains(SourceType.Words)) &&
			(options.source.contains(SourceType.QSO) || options.source.contains(SourceType.Callsigns))) {
			throw new IllegalArgumentException("Cannot mix word generators with callsigns or QSO");
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
				"-source must be followed by a source type [all|letters|numbers|punctuation|prosigns|set|codes|words|callsigns|qso]");
	}

	private Optional<Source.PlayType> nextPlayArg() {
		if (hasNextArg()) {
			final Optional<Source.PlayType> out = Source.PlayType.fromString(nextArg());
			if (out.isPresent()) {
				return out;
			}
		}
		throw new IllegalArgumentException(
				"-play must be followed by an input type [file|stdin|-]");
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
		println("@|yellow -interactive|@          - Interactively query for what you heard,");
		println("                        to test/assess your recognition progress. If not given,");
		println("                        just plays or records.");
		//           01234567890123456789012345678901234567890123456789012345678901234567890123456789
		println("@|yellow -record <filename>|@    - Records the Morse to a .wav file. Can't be used");
		println("                        with -interactive mode. Otherwise, just plays to speakers.");
		println("@|yellow -contents <filename>|@  - Appends the text that's sent to a .txt file.");
		println("                        Can't be used with -interactive mode. Must be used with");
		println("                        -record mode. Otherwise, just displays on screen.");
		println("@|yellow -length <1..9|random>|@ - Fixed or random length of sent character groups.");
		println("                        Default is random (up to 9) if not given.");
		println("@|yellow -groupsize <10..250>|@ - Number of words in a group. Default is 25.");
		println("");
		println("@|bold Source (set of characters to randomise, and train with):|@");
		println("@|cyan -source [all|letters|numbers|punctuation|prosigns|set]|@");
		println("@|cyan -source set '....'|@");
		println("                      - Use letters, numbers etc. as the characters");
		println("                        to send, or with set '...', use the specific");
		println("                        characters specified.");
		println("                        Default is 'all' if not given.");
		println("@|bold Source (set of words to randomise, and train with):|@");
		println("@|cyan -source [words|codes|callsigns|qso]|@");
		println("@|cyan -source words|@");
		//           01234567890123456789012345678901234567890123456789012345678901234567890123456789
		println("                      - Play random words (from ~500 common words, varying length).");
		println("@|cyan -source codes|@");
		println("                      - Play random Q-codes and other abbreviations.");
		println("@|cyan -source callsigns|@");
		println("                      - Play random callsigns.");
		println("@|cyan -source QSO|@");
		println("                      - Play a generated QSO.");
		println("");
		println("@|bold Play (text to play/generate):|@");
		println("@|green -play [stdin|-|file|text]|@");
		println("@|green -play stdin   or  -play -|@");
		println("                      - Play the text from standard input.");
		println("@|green -play file <filename>|@");
		println("                      - Play the text read from a file.");
		println("@|green -play text <some text>|@");
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
		println("");
		println("(Words and codes taken from 'The Art & Skill of Radio Telegraphy', by William G.");
		println(" Pierpont N0HFF (SK) and 'Morse Code for Radio Amateurs' by Roger Cooke G3LDI)");
	}
}
