package org.devzendo.morsetrainer2.cmd;

import static org.devzendo.morsetrainer2.cmd.AnsiHelper.println;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.devzendo.commoncode.logging.Logging;
import org.devzendo.commoncode.resource.ResourceLoader;
import org.devzendo.morsetrainer2.controller.Controller;
import org.devzendo.morsetrainer2.controller.ControllerFactory;
import org.devzendo.morsetrainer2.iterator.PartyMorseCharacterIterator;
import org.devzendo.morsetrainer2.iterator.PartyMorseCharacterIteratorFactory;
import org.devzendo.morsetrainer2.player.Player;
import org.devzendo.morsetrainer2.player.PlayerFactory;
import org.devzendo.morsetrainer2.prefs.PrefsFactory;
import org.devzendo.morsetrainer2.qso.CallsignGenerator;
import org.devzendo.morsetrainer2.qso.QSOGenerator;
import org.devzendo.morsetrainer2.source.Source.PlayType;
import org.devzendo.morsetrainer2.source.Source.SourceType;
import org.devzendo.morsetrainer2.stats.StatsFactory;
import org.devzendo.morsetrainer2.stats.StatsStore;
import org.fusesource.jansi.AnsiConsole;
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
        AnsiConsole.systemInstall();

		final Properties properties = getPropertiesResource();
		try {
			final PrefsFactory prefsFactory = new PrefsFactory("morsetrainer2", "morsetrainer2.ini");
			if (!prefsFactory.prefsDirectoryExists()) {
				if (!prefsFactory.createPrefsDirectory()) {
					throw new IllegalStateException("Can't create preferences directory '" + prefsFactory.getPrefsDir() + "'");
				}
			}

			final StatsFactory statsFactory = new StatsFactory(prefsFactory.getPrefsDir());
			final StatsStore statsStore = statsFactory.open();

			final CommandLineParser parser = new CommandLineParser(finalArgList, properties);
			final Options options = parser.getOptions();

			final CallsignGenerator callsignGenerator = new CallsignGenerator();
			final QSOGenerator qsoGenerator = new QSOGenerator(callsignGenerator);
			final PartyMorseCharacterIterator it = new PartyMorseCharacterIteratorFactory(options.groupSize, options.length, options.source, options.sourceChars, options.sourceWords, options.play, options.playString, callsignGenerator, qsoGenerator).create();

			final Player player = PlayerFactory.createPlayer(options.freqHz, options.wpm, options.fwpm, options.recordFile);
			final Optional<PrintStream> contentsPrintStream = contentsPrintStream(options.contentsFile);
			printContentsBanner(options.wpm, options.fwpm, options.source, options.play, options.recordFile, contentsPrintStream);
			final Controller ctrl = ControllerFactory.createController(options.interactive, it, player, statsStore, contentsPrintStream);

			ctrl.prepare();
			player.play("VVV ");
			ctrl.start();
			ctrl.finish();
			contentsPrintStream.ifPresent(ps -> { ps.println(); ps.println(); });
		} catch (final Exception e) {
			println("@|bold,red " + e.getMessage() + "|@");
			LOGGER.error(e.getMessage(), e);
			System.exit(1);
		}
	}

	private static void printContentsBanner(final Integer wpm, final Integer fwpm, final Set<SourceType> source, final Optional<PlayType> play, final Optional<File> recordFile, final Optional<PrintStream> contentsPrintStream) {
		contentsPrintStream.ifPresent(ps -> {
			final StringBuilder sb = new StringBuilder();
			sb.append(recordFile.get().getName());
			sb.append(" ");
			sb.append(wpm);
			sb.append(" WPM ");
			if (fwpm != wpm) {
				sb.append(fwpm);
				sb.append(" Farnsworth WPM ");
			}
			if (!source.isEmpty()) {
				sb.append("Source: ");
				sb.append(source.stream().map(SourceType::toString).collect(Collectors.joining(",")));
			}
			if (play.isPresent()) {
				sb.append("Play: ");
				sb.append(play.get().toString());
			}
			final String string = sb.toString();
			ps.println(string);
			final String repeat = StringUtils.repeat('-', string.length());
			ps.println(repeat);
		});
	}

	private static Optional<PrintStream> contentsPrintStream(final Optional<File> contentsFile) {
		if (contentsFile.isPresent()) {
			final File file = contentsFile.get();
			try {
				return Optional.of(new PrintStream(new FileOutputStream(file, true)));
			} catch (final FileNotFoundException e) {
				throw new IllegalStateException("Could not create contents file '" + file.getAbsolutePath() + "': " + e.getMessage());
			}
		}
		return Optional.empty();
	}
}
