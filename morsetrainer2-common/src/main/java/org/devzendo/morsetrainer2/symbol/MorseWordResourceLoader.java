package org.devzendo.morsetrainer2.symbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.devzendo.commoncode.resource.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MorseWordResourceLoader {

	private static Logger LOGGER = LoggerFactory.getLogger(MorseWordResourceLoader.class);

	public static List<MorseWord> wordsFromResource(final String resourcePath) {
		final List<MorseWord> words = new ArrayList<>();
		final TextToMorseCharacterParser parser = parseResource(resourcePath);
		ArrayList<MorseCharacter> mcs = new ArrayList<>();
		while (parser.hasNext()) {
			final MorseCharacter mc = parser.next();
			if (mc == MorseCharacter.SPC) {
				if (mcs.size() > 0) {
					debugLogWord(mcs);
					words.add(new MorseWord(mcs));
				}
				mcs = new ArrayList<MorseCharacter>();
			} else {
				mcs.add(mc);
			}
		}
		if (mcs.size() > 0) {
			debugLogWord(mcs);
			words.add(new MorseWord(mcs));
		}
		LOGGER.debug("Returning {} words", words.size());
		return words;
	}

	private static void debugLogWord(final ArrayList<MorseCharacter> sb) {
		if (LOGGER.isDebugEnabled()) {
			final String word = sb.stream().map(mcc -> mcc.toString()).collect(Collectors.joining());
			LOGGER.debug("Adding word '{}'", word);
		}
	}

	private static TextToMorseCharacterParser parseResource(final String resourcePath) {
		try {
			final TextToMorseCharacterParser parser = new TextToMorseCharacterParser();
			try (final BufferedReader br = new BufferedReader(new InputStreamReader(ResourceLoader.getResourceInputStream(resourcePath)))) {
				String line = null;
				do {
					line = br.readLine();
					if (line != null) {
						parser.addString(line);
						parser.addString(" ");
					}
				} while (line != null);
			}
			return parser;
		} catch (final IOException ioe) {
			throw new IllegalStateException("Failed reading resource " + resourcePath + ": " + ioe.getMessage(), ioe);
		}
	}
}
