package org.devzendo.morsetrainer2.iterator;

import java.util.Optional;
import java.util.Set;

import org.devzendo.morsetrainer2.qso.CallsignGenerator;
import org.devzendo.morsetrainer2.qso.QSOGenerator;
import org.devzendo.morsetrainer2.source.Source.PlayType;
import org.devzendo.morsetrainer2.source.Source.SourceType;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.MorseWord;

public class PartyMorseCharacterIteratorFactory {

	private final Optional<Integer> length;
	private final Set<SourceType> source;
	private final Set<MorseCharacter> sourceChars;
	private final Set<MorseWord> sourceWords;
	private final CallsignGenerator callsignGenerator;
	private final QSOGenerator qsoGenerator;
	private final Optional<PlayType> play;
	private final String playString;
	private final Integer groupSize;

	public PartyMorseCharacterIteratorFactory(final Integer groupSize, final Optional<Integer> length, final Set<SourceType> source, final Set<MorseCharacter> sourceChars,
			final Set<MorseWord> sourceWords, final Optional<PlayType> play, final String playString, final CallsignGenerator callsignGenerator,
			final QSOGenerator qsoGenerator) {
		this.groupSize = groupSize;
		this.length = length;
		this.source = source;
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
			default:
				throw new IllegalArgumentException("Unknown value of play: " + play);
			}
		}

		if (sourceChars.isEmpty() && sourceWords.isEmpty()) {
			throw new IllegalArgumentException("No value of source or play");
		}

		if (source.contains(SourceType.QSO)) {
			return qsoGenerator.iterator();
		}
		if (source.contains(SourceType.Callsigns)) {
			return callsignGenerator.iterator();
		}

		if (sourceWords.isEmpty()) {
			return new RandomGroupingSetIterator(groupSize, length, sourceChars.toArray(new MorseCharacter[0]));
		} else {
			return new RandomGroupingWordIterator(groupSize, length, sourceWords);
		}
	}
}
