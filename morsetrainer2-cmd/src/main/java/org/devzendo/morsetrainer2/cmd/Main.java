package org.devzendo.morsetrainer2.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.devzendo.commoncode.logging.Logging;
import org.devzendo.commoncode.resource.ResourceLoader;
import org.devzendo.morsetrainer2.cmd.Options.Source;
import org.devzendo.morsetrainer2.qso.CallsignGenerator;
import org.devzendo.morsetrainer2.qso.QSOGenerator;
import org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	static Properties getPropertiesResource() {
		final String propertiesResourceName = "morsetrainer2.properties";
		final Properties propertiesResource = ResourceLoader.readPropertiesResource(propertiesResourceName);
		if (propertiesResource == null) {
			LOGGER.error("Could not load " + propertiesResourceName);
			throw new IllegalStateException();
		}
		return propertiesResource;
	}

	private int cmdIndex = 0;
	private List<String> finalArgList;
	private Options options = new Options();

	public Main(final List<String> finalArgList, final Properties properties) {
		this.finalArgList = finalArgList;
		final CallsignGenerator callsignGenerator = new CallsignGenerator();
		final QSOGenerator qsoGenerator = new QSOGenerator(callsignGenerator);

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
				System.out.println("source is " + options.source);
				switch (options.source) {
				case All:
				case Letters:
				case Numbers:
				case Prosigns:
				case Punctuation:
					options.sourceString = options.source.content();
					break;
				case QSO:
					options.sourceString = qsoGenerator.generate();
					break;
				case Callsigns:
					options.sourceString = callsignGenerator.generate();
					break;
				case Set:
					if (hasNextArg()) {
						final String setString = nextArg();
						options.sourceString = TextToMorseCharacterParser.parseToString(setString);
					} else {
						throw new IllegalArgumentException("-source <set> must be followed by a string of source characters");
					}
					break;
				case File:
					if (hasNextArg()) {
						options.sourceString = TextToMorseCharacterParser.parseToString(readFile(new File(nextArg())));
					} else {
						throw new IllegalArgumentException("-source <file> must be followed by a file name");
					}
					break;
				case Stdin:
					options.sourceString = TextToMorseCharacterParser.parseToString(readStdin());
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
						throw new IllegalArgumentException("Cannot overwrite existing recording '" + recordingFile.getAbsolutePath() + "'");
					}
					options.recordFile = Optional.of(recordingFile);
				} else {
					throw new IllegalArgumentException("-record <file> must be followed by a file name");
				}
				
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
		
		//final StreamGenerator streamGenerator = StreamGeneratorFactory(options.source).create();
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

	private Integer nextNumArg(int low, int high, String name) {
		if (hasNextArg()) {
			final String nextArg = nextArg();
			try {
				final Integer out = Integer.parseInt(nextArg);
				if (out < low || out > high) {
					LOGGER.error("The " + name + " '" + nextArg + "' is not in the range " + low + " to " + high);
				} else {
					return out;
				}
			} catch (NumberFormatException nfe) {
				LOGGER.error("The " + name + " '" + nextArg + "' is not an integer");
			}
		}
		throw new IllegalArgumentException("-" + name + " must be in the range " + low + " to " + high);
	}

	private Source nextSourceArg() {
		if (hasNextArg()) {
			final Optional<Source> out = Options.Source.fromString(nextArg());
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

	private String readStdin() {
		return readInputStream(System.in);
	}

	private String readInputStream(final InputStream in) {
		try {
			final StringBuilder sb = new StringBuilder();
			final BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
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
	
	private void usage() {
		//           01234567890123456789012345678901234567890123456789012345678901234567890123456789
		LOGGER.info("java -jar morsetrainer2.jar [options] [source]");
		LOGGER.info("Options:");
		LOGGER.info("-wpm <words per min>  - Set the speed in words per minute.");
		LOGGER.info("                        Default is 12 WPM if not given.");
		LOGGER.info("                        Must be between 12 and 60.");
		LOGGER.info("-fwpm <words per min> - Set the Farnsworth speed in words per minute.");
		LOGGER.info("                        Default matches the WPM if not given.");
		LOGGER.info("-freq <Hz>            - Set the tone frequency in Hertz.");
		LOGGER.info("                        Default is 600 Hz if not given.");
		LOGGER.info("-interactive          - Interactively query for what you heard, to test/assess");
		LOGGER.info("                        your recognition progress. If not given, just plays/");
		LOGGER.info("                        or records.");
		LOGGER.info("-record <filename>    - Records the Morse to a .wav file. Can't be used with");
		LOGGER.info("                        -interactive mode. If not given, just plays to speakers.");
		LOGGER.info("");
		LOGGER.info("Source:");
		LOGGER.info("-source [all|letters|numbers|punctuation|prosigns|callsigns|qso|set|");
		LOGGER.info("         stdin|-|file]");
		LOGGER.info("-source set '....'");
		LOGGER.info("                      - Use letters, numbers etc. as the characters");
		LOGGER.info("                        to send, or with set '...', use the specific");
		LOGGER.info("                        characters specified.");
		LOGGER.info("                        Default is 'all' if not given.");
		LOGGER.info("-source stdin   or  -source -");
		LOGGER.info("                      - Read the set of characters from standard input.");
		LOGGER.info("-source file <filename>");
		LOGGER.info("                      - Read the set of characters from a file.");
		LOGGER.info("");
		LOGGER.info("-? or -help or -usage - Show this usage summary");
		LOGGER.info("");
		LOGGER.info("Log4j output control options:");
		LOGGER.info("-debug                - Set the log level to debug (default is info).");
		LOGGER.info("-warn                 - Set the log level to warning.");
		LOGGER.info("-level                - Show log levels of each log line output.");
		LOGGER.info("-classes              - Show class names in each log line output.");
		LOGGER.info("-threads              - Show thread names in each log line output.");
		LOGGER.info("-times                - Show timing data in each log line output.");
	}

	private void finish() {
		System.exit(0);
	}

	private void showVersion(final Properties properties) {
		final String version = properties.getProperty("version");
		LOGGER.info("MorseTrainer2 version " + version);
	}

	public static void main(final String[] args) {
		final Logging logging = Logging.getInstance();
		final List<String> finalArgList = logging.setupLoggingFromArgs(Arrays.asList(args));
		final Properties properties = getPropertiesResource();
		try {
			new Main(finalArgList, properties);
		} catch (final Exception e) {
			LOGGER.error(e.getMessage(), e);
			System.exit(1);
		}
	}
}
