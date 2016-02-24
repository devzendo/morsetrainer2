package org.devzendo.morsetrainer2.iterator;

import java.util.Optional;

import org.devzendo.morsetrainer2.qso.CallsignGenerator;
import org.devzendo.morsetrainer2.qso.QSOGenerator;
import org.devzendo.morsetrainer2.source.Source.SourceType;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser;

public class PartyMorseCharacterIteratorFactory {

	private Optional<Integer> length;
	private SourceType source;
	private String sourceString;
	private CallsignGenerator callsignGenerator;
	private QSOGenerator qsoGenerator;

	public PartyMorseCharacterIteratorFactory(final Optional<Integer> length, final SourceType source, final String sourceString,
			final CallsignGenerator callsignGenerator, final QSOGenerator qsoGenerator) {
		this.length = length;
		this.source = source;
		this.sourceString = sourceString;
		this.callsignGenerator = callsignGenerator;
		this.qsoGenerator = qsoGenerator;
	}

	public PartyMorseCharacterIterator create() {
		switch (source) {
		case All:
		case Letters:
		case Numbers:
		case Prosigns:
		case Punctuation:
		case Set:
			final MorseCharacter[] sourceSetArray = TextToMorseCharacterParser.parseToSetAsArray(sourceString);
			return new RandomGroupingIterator(length, sourceSetArray);
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
			throw new IllegalStateException("Iterator not provided for " + source);
		}
	}

}
