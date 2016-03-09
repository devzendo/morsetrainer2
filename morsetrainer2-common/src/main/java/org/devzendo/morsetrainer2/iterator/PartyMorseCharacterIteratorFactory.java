package org.devzendo.morsetrainer2.iterator;

import java.util.Optional;

import org.devzendo.morsetrainer2.qso.CallsignGenerator;
import org.devzendo.morsetrainer2.qso.QSOGenerator;
import org.devzendo.morsetrainer2.source.Source.PlayType;
import org.devzendo.morsetrainer2.source.Source.SourceType;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser;

public class PartyMorseCharacterIteratorFactory {

	private final Optional<Integer> length;
	private final Optional<SourceType> source;
	private final String sourceString;
	private final CallsignGenerator callsignGenerator;
	private final QSOGenerator qsoGenerator;
	private final Optional<PlayType> play;

	public PartyMorseCharacterIteratorFactory(final Optional<Integer> length, final Optional<SourceType> source,
			final Optional<PlayType> play, final String sourceString, final CallsignGenerator callsignGenerator,
			final QSOGenerator qsoGenerator) {
		this.length = length;
		this.source = source;
		this.play = play;
		this.sourceString = sourceString;
		this.callsignGenerator = callsignGenerator;
		this.qsoGenerator = qsoGenerator;
	}

	public PartyMorseCharacterIterator create() {
		if (play.isPresent()) {
			switch (play.get()) {
			case File:
			case Stdin:
			case Text:
				return new VerbatimIterator(sourceString);
			// No source string is set for these....
			case QSO:
				return qsoGenerator.iterator();
			case Callsigns:
				return callsignGenerator.iterator();
			default:
				throw new IllegalArgumentException("Unknown value of play: " + play);
			}
		}
		if (source.isPresent()) {
			switch (source.get()) {
			case All:
			case Letters:
			case Numbers:
			case Prosigns:
			case Punctuation:
			case Set:
				final MorseCharacter[] sourceSetArray = TextToMorseCharacterParser.parseToSetAsArray(sourceString);
				return new RandomGroupingIterator(length, sourceSetArray);
			default:
				throw new IllegalArgumentException("Unknown value of source: " + play);
			}
		}
		throw new IllegalArgumentException("No value of source or play");
	}
}
