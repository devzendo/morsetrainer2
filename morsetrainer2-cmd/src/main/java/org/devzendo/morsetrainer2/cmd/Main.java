package org.devzendo.morsetrainer2.cmd;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.devzendo.commoncode.logging.Logging;
import org.devzendo.commoncode.resource.ResourceLoader;
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

	private boolean hasNextArg() {
		return cmdIndex < finalArgList.size();
	}

	private String nextArg() {
		return finalArgList.get(cmdIndex++);
	}

	public Main(final List<String> finalArgList, final Properties properties) {
		this.finalArgList = finalArgList;
		Integer wpm = null;
		Integer fwpm = null;
		Integer freqHz = null;

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
				wpm = nextNumArg(12, 60, "wpm");
				break;
			case "-fwpm":
				fwpm = nextNumArg(12, 60, "fwpm");
				break;
			}
		}
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

	private void usage() {
		LOGGER.info("java -jar morsetrainer2.jar [options]");
		LOGGER.info("Options:");
		LOGGER.info("-wpm <words per min>  - Set the speed in words per minute");
		LOGGER.info("                        Default is 12 WPM if not given");
		LOGGER.info("                        Must be between 12 and 60");
		LOGGER.info("-fwpm <words per min> - Set the Farnsworth speed in words per minute");
		LOGGER.info("                        Default matches the WPM if not given");
		LOGGER.info("-freq <Hz>            - Set the tone frequency in Hertz");
		LOGGER.info("                        Default is 600 Hz if not given");
		LOGGER.info("");
		LOGGER.info("-version              - Show the version number");
		LOGGER.info("-? or -help or -usage - Show this usage summary");
		LOGGER.info("");
		LOGGER.info("Log4j output control options:");
		LOGGER.info("-debug                - Set the log level to debug (default is info)");
		LOGGER.info("-warn                 - Set the log level to warning");
		LOGGER.info("-level                - Show log levels of each log line output");
		LOGGER.info("-classes              - Show class names in each log line output");
		LOGGER.info("-threads              - Show thread names in each log line output");
		LOGGER.info("-times                - Show timing data in each log line output");
	}

	private void finish() {
		System.exit(0);
	}

	private void fail() {
		System.exit(1);
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
		}
	}
}
