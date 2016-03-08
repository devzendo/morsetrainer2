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
	private final SourceType source;
	private final String sourceString;
	private final CallsignGenerator callsignGenerator;
	private final QSOGenerator qsoGenerator;
	private final PlayType play;

	public PartyMorseCharacterIteratorFactory(final Optional<Integer> length, final SourceType source,
			final PlayType play, final String sourceString, final CallsignGenerator callsignGenerator,
			final QSOGenerator qsoGenerator) {
		this.length = length;
		this.source = source;
		this.play = play;
		this.sourceString = sourceString;
		this.callsignGenerator = callsignGenerator;
		this.qsoGenerator = qsoGenerator;
	}

	public PartyMorseCharacterIterator create() {
		switch (play) {
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
			switch (source) {
			case All:
			case Letters:
			case Numbers:
			case Prosigns:
			case Punctuation:
			case Set:
			default:
				final MorseCharacter[] sourceSetArray = TextToMorseCharacterParser.parseToSetAsArray(sourceString);
				return new RandomGroupingIterator(length, sourceSetArray);
			}
		}
	}

}
