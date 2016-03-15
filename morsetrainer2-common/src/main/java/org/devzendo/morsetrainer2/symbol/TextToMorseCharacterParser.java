package org.devzendo.morsetrainer2.symbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextToMorseCharacterParser implements Iterator<MorseCharacter> {

	private static Logger LOGGER = LoggerFactory.getLogger(TextToMorseCharacterParser.class);

	/**
	 * Parse any ASCII text, possibly containing <PR> style prosigns to an array
	 * of MorseCharacters. Unknown characters are omitted; multiple spaces are
	 * preserved; other white space ignored.
	 *
	 * Builds up an internal list, then converts to an array - may not be the
	 * best use of memory!
	 *
	 * @param string
	 *            any ASCII text.
	 * @return possibly empty array, never null.
	 */
	public static MorseCharacter[] parse(final String string) {
		LOGGER.debug("Parsing '" + string + "'");
		final List<MorseCharacter> list = new ArrayList<>();

		if (string != null && string.length() != 0) {
			final TextToMorseCharacterParser parser = new TextToMorseCharacterParser();
			parser.addString(string);
			while (parser.hasNext()) {
				list.add(parser.next());
			}
		}

		return list.toArray(new MorseCharacter[0]);
	}

	/**
	 * Parse any ASCII text, possibly containing <PR> style prosigns to an array
	 * of MorseCharacters. Unknown characters are omitted; multiple spaces are
	 * preserved; other white space ignored; duplicates are removed (thus forming
	 * a set); order is not preserved.
	 *
	 * Builds up an internal set, then converts to an array - may not be the
	 * best use of memory, but ensures random access is possible!
	 *
	 * @param string
	 *            any ASCII text.
	 * @return possibly empty array, never null.
	 */
	public static MorseCharacter[] parseToSetAsArray(final String string) {
		return parseToSet(string).toArray(new MorseCharacter[0]);
	}

	/**
	 * Parse any ASCII text, possibly containing <PR> style prosigns to a set
	 * of MorseCharacters. Unknown characters are omitted; multiple spaces are
	 * preserved; other white space ignored; duplicates are removed (thus forming
	 * a set); order is not preserved.
	 *
	 * @param string
	 *            any ASCII text.
	 * @return possibly empty set, never null.
	 */
	public static Set<MorseCharacter> parseToSet(final String string) {
		LOGGER.debug("Parsing '" + string + "'");
		final Set<MorseCharacter> set = new HashSet<>();

		if (string != null && string.length() != 0) {
			final TextToMorseCharacterParser parser = new TextToMorseCharacterParser();
			parser.addString(string);
			while (parser.hasNext()) {
				set.add(parser.next());
			}
		}
		return set;
	}

	/**
	 * Parse any ASCII text, possibly containing <PR> style prosigns to an array
	 * of MorseCharacters. Unknown characters are omitted; multiple spaces are
	 * preserved; other white space ignored.
	 *
	 * Returns a list that's fast for iterating over. An ArrayList, if you need
	 * to know. In a test, this benchmarked at 10s to iterate over with many items;
	 * a LinkedList benchmarked at 37s.
	 *
	 * @param string
	 *            any ASCII text.
	 * @return possibly empty List, never null.
	 */
	public static List<MorseCharacter> parseToList(final String string) {
		LOGGER.debug("Parsing '" + string + "'");
		final List<MorseCharacter> out = new ArrayList<>();

		if (string != null && string.length() != 0) {
			final TextToMorseCharacterParser parser = new TextToMorseCharacterParser();
			parser.addString(string);
			while (parser.hasNext()) {
				out.add(parser.next());
			}
		}

		return out;
	}

	/**
	 * Parse any ASCII text, possibly containing <PR> style prosigns to an array
	 * of MorseCharacters. Unknown characters are omitted; multiple spaces are
	 * preserved; other white space ignored.
	 *
	 * Builds up an internal list, then converts to a String - may not be the
	 * best use of memory!
	 *
	 * @param string
	 *            any ASCII text.
	 * @return possibly empty String, never null.
	 */
	public static String parseToString(final String string) {
		LOGGER.debug("Parsing '" + string + "'");
		final StringBuilder sb = new StringBuilder();;

		if (string != null && string.length() != 0) {
			final TextToMorseCharacterParser parser = new TextToMorseCharacterParser();
			parser.addString(string);
			while (parser.hasNext()) {
				final MorseCharacter next = parser.next();
				sb.append(next.toString());
			}
		}

		return sb.toString();
	}

	private final LinkedList<Character> inputText = new LinkedList<>();
	private MorseCharacter store = null;
	private boolean inProsignScan = false;
	private StringBuilder prosignBuffer = new StringBuilder();

	public void addString(final String anyString) {
		LOGGER.debug("Adding input string '" + anyString + "'");
		if (anyString != null && anyString.length() != 0) {
			for (final char c : anyString.toUpperCase().toCharArray()) {
				LOGGER.debug("Adding input character '" + c + "'");
				inputText.add(new Character(c));
			}
		}
	}

	@Override
	public boolean hasNext() {
		if (inputText.isEmpty()) {
			LOGGER.debug("Out of input text");
			return false;
		}
		final Character top = inputText.pop();
		LOGGER.debug("Testing character '" + top + "'");
		if (inProsignScan) {
			LOGGER.debug("In prosign scan");
			if (top.equals('>')) {
				// End of prosign.
				inProsignScan = false;
				final String prosignText = prosignBuffer.toString();
				final Optional<MorseCharacter> opt = MorseCharacter.fromProsignText(prosignText);
				prosignBuffer = new StringBuilder();
				if (opt.isPresent()) {
					store(opt.get());
					return true;
				}
				LOGGER.debug("Discarding bad prosign '" + prosignText + "'");
				// Not a valid prosign, continue scan
			} else {
				// Could be a prosign, continue scan
				LOGGER.debug("Adding '" + top + "' to prosign buffer");
				prosignBuffer.append(top);
			}
			return hasNext();
		} else {
			LOGGER.debug("Not in prosign scan");
			final Optional<MorseCharacter> opt = MorseCharacter.fromChar(top);
			if (opt.isPresent()) {
				store(opt.get());
				return true;
			}
			if (!top.equals('<')) {
				// it isn't a valid single char, and doesn't start a prosign, so it's bad. discard.
				LOGGER.debug("Discarding bad input '" + top + "'");
				return hasNext();
			}
			// Start of prosign.
			LOGGER.debug("Starting prosign scan");
			inProsignScan = true;
			prosignBuffer = new StringBuilder();
			return hasNext();
		}
	}

	private void store(final MorseCharacter morseCharacter) {
		LOGGER.debug("Storing " + morseCharacter);
		store = morseCharacter;
	}

	@Override
	public MorseCharacter next() {
		return store;
	}
}
