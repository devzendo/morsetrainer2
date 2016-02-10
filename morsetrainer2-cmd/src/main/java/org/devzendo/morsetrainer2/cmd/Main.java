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
			}
		}
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
