package org.devzendo.morsetrainer2.iterator;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.devzendo.morsetrainer2.qso.CallsignGenerator;
import org.devzendo.morsetrainer2.qso.QSOGenerator;
import org.devzendo.morsetrainer2.source.Source.PlayType;
import org.devzendo.morsetrainer2.source.Source.SourceType;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.TextToMorseCharacterParser;

public class PartyMorseCharacterIteratorFactory {

	private final Optional<Integer> length;
	private final Set<SourceType> source;
	private final Set<MorseCharacter> sourceChars;
	private final CallsignGenerator callsignGenerator;
	private final QSOGenerator qsoGenerator;
	private final Optional<PlayType> play;
	private final String playString;

	public PartyMorseCharacterIteratorFactory(final Optional<Integer> length, final Set<SourceType> source, final Set<MorseCharacter> sourceChars,
			final Optional<PlayType> play, final String playString, final CallsignGenerator callsignGenerator,
			final QSOGenerator qsoGenerator) {
		this.length = length;
		this.source = source;
		this.sourceChars = sourceChars;
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

		if (source.isEmpty()) {
			throw new IllegalArgumentException("No value of source or play");
		}

		final Set<MorseCharacter> charSet = new HashSet<>(sourceChars);
		source.forEach(s -> charSet.addAll(TextToMorseCharacterParser.parseToSet(s.content())));
		return new RandomGroupingIterator(length, charSet.toArray(new MorseCharacter[0]));
	}
}
