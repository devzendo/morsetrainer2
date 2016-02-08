package org.devzendo.morsetrainer2.symbol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextToMorseCharacterParser implements Iterator<MorseCharacter> {

	private static Logger LOGGER = LoggerFactory.getLogger(TestTextToMorseCharacterParser.class);
	
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

	private final LinkedList<Character> inputText = new LinkedList<>();
	private MorseCharacter store = null;
	private boolean inProsignScan = false;
	private StringBuilder prosignBuffer = new StringBuilder();
	
	public void addString(final String anyString) {
		LOGGER.debug("Adding input string '" + anyString + "'");
		if (anyString != null && anyString.length() != 0) {
			for (char c : anyString.toUpperCase().toCharArray()) {
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
