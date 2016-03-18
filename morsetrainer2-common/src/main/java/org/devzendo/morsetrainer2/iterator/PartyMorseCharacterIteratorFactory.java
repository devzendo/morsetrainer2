package org.devzendo.morsetrainer2.iterator;

import java.util.Optional;
import java.util.Set;

import org.devzendo.morsetrainer2.qso.CallsignGenerator;
import org.devzendo.morsetrainer2.qso.QSOGenerator;
import org.devzendo.morsetrainer2.source.Source.PlayType;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.MorseWord;

public class PartyMorseCharacterIteratorFactory {

	private final Optional<Integer> length;
	private final Set<MorseCharacter> sourceChars;
	private final Set<MorseWord> sourceWords;
	private final CallsignGenerator callsignGenerator;
	private final QSOGenerator qsoGenerator;
	private final Optional<PlayType> play;
	private final String playString;

	public PartyMorseCharacterIteratorFactory(final Optional<Integer> length, final Set<MorseCharacter> sourceChars,
			final Set<MorseWord> sourceWords, final Optional<PlayType> play, final String playString, final CallsignGenerator callsignGenerator,
			final QSOGenerator qsoGenerator) {
		this.length = length;
		this.sourceChars = sourceChars;
		this.sourceWords = sourceWords;
		this.play = play;
		this.playString = playString;
		this.callsignGenerator = callsignGenerator;
		this.qsoGenerator = qsoGenerator;
	}

	public PartyMorseCharacterIterator create() {
		if (play.isPresent()) {
			switch (play.get()) {
			case File:
			case Stdin:
			case Text:
				return new VerbatimIterator(playString);
			// No source string is set for these....
			case QSO:
				return qsoGenerator.iterator();
			case Callsigns:
				return callsignGenerator.iterator();
			default:
				throw new IllegalArgumentException("Unknown value of play: " + play);
			}
		}

		if (sourceChars.isEmpty() && sourceWords.isEmpty()) {
			throw new IllegalArgumentException("No value of source or play");
		}

		if (!sourceWords.isEmpty()) {
			return new RandomGroupingWordIterator(length, sourceWords);
		} else {
			return new RandomGroupingSetIterator(length, sourceChars.toArray(new MorseCharacter[0]));
		}
	}

}
