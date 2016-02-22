package org.devzendo.morsetrainer2.cmd;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.devzendo.commoncode.logging.Logging;
import org.devzendo.commoncode.resource.ResourceLoader;
import org.devzendo.morsetrainer2.qso.CallsignGenerator;
import org.devzendo.morsetrainer2.qso.QSOGenerator;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacterIterator;
import org.devzendo.morsetrainer2.symbol.PartyMorseCharacterIteratorFactory;
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

	public static void main(final String[] args) {
		final Logging logging = Logging.getInstance();
		final List<String> finalArgList = logging.setupLoggingFromArgs(Arrays.asList(args));
		final Properties properties = getPropertiesResource();
		try {
			final CommandLineParser parser = new CommandLineParser(finalArgList, properties);
			final Options options = parser.getOptions();
			final CallsignGenerator callsignGenerator = new CallsignGenerator();
			final QSOGenerator qsoGenerator = new QSOGenerator(callsignGenerator);
			final PartyMorseCharacterIterator it = new PartyMorseCharacterIteratorFactory(options.source, options.sourceString, callsignGenerator, qsoGenerator).create();

		} catch (final Exception e) {
			LOGGER.error(e.getMessage(), e);
			System.exit(1);
		}
	}

}
