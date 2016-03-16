package org.devzendo.morsetrainer2.symbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.devzendo.commoncode.resource.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MorseWordResourceLoader {

	private static Logger LOGGER = LoggerFactory.getLogger(MorseWordResourceLoader.class);

	public static List<String> wordsFromResource(final String resourcePath) {
		final List<String> words = new ArrayList<>();
		final TextToMorseCharacterParser parser = parseResource(resourcePath);
		StringBuilder sb = new StringBuilder();
		while (parser.hasNext()) {
			final MorseCharacter mc = parser.next();
			if (mc == MorseCharacter.SPC) {
				if (sb.length() > 0) {
					final String word = sb.toString();
					LOGGER.debug("Adding word '{}'", word);
					words.add(word);
				}
				sb = new StringBuilder();
			} else {
				sb.append(mc.toString());
			}
		}
		if (sb.length() > 0) {
			words.add(sb.toString());
		}
		LOGGER.debug("Returning {} words", words.size());
		return words;
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
