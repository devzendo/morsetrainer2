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

	private static Properties getPropertiesResource() {
		final String propertiesResourceName = "morsetrainer2.properties";
		final Properties propertiesResource = ResourceLoader.readPropertiesResource(propertiesResourceName);
		if (propertiesResource == null) {
			LOGGER.error("Could not load " + propertiesResourceName);
			throw new IllegalStateException();
		}
		return propertiesResource;
	}

	public Main(final List<String> finalArgList, final Properties properties) {
		Integer wpm = null;
		Integer fwpm = null;
		Integer freqHz = null;

		for (int i = 0; i < finalArgList.size(); i++) {
			final String arg = finalArgList.get(i);
			switch (arg) {
			case "-version":
				showVersion(properties);
				finish();
			case "-usage":
			case "-?":
			case "-help":
				usage();
				finish();
			}
		}
	}

	private void usage() {
		LOGGER.info("java -jar morsetrainer2.jar [options]");
		LOGGER.info("Options:");
		LOGGER.info("-wpm <words per min>  - set the speed in words per minute");
		LOGGER.info("                        Default is 12 WPM if not given");
		LOGGER.info("-fwpm <words per min> - set the Farnsworth speed in words per minute");
		LOGGER.info("                        Default matches the WPM if not given");
		LOGGER.info("-freq <Hz>            - set the tone frequency in Hertz");
		LOGGER.info("                        Default is 600 Hz if not given");
		LOGGER.info("");
		LOGGER.info("-version              - show the version number");
		LOGGER.info("-? or -help or -usage - show this usage summary");
		LOGGER.info("");
        LOGGER.info("Log4j output control options:");
        LOGGER.info("-debug                - set the log level to debug (default is info)");
        LOGGER.info("-warn                 - set the log level to warning");
        LOGGER.info("-level                - show log levels of each log line output");
        LOGGER.info("-classes              - show class names in each log line output");
        LOGGER.info("-threads              - show thread names in each log line output");
        LOGGER.info("-times                - show timing data in each log line output");
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
		new Main(finalArgList, properties);
	}
}
