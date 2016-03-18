package org.devzendo.morsetrainer2.cmd;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.devzendo.morsetrainer2.source.Source;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.MorseWord;

public class Options {
	public Integer wpm;
	public Integer fwpm;
	public Integer freqHz;

	public Set<Source.SourceType> source = new HashSet<>();
	public Set<MorseCharacter> sourceChars = new HashSet<>();
	public Set<MorseWord> sourceWords = new HashSet<>();

	public Optional<Source.PlayType> play = Optional.empty();
	public String playString = "";

	public boolean interactive = false;
	public Optional<File> recordFile = Optional.empty();
	public Optional<Integer> length = Optional.empty(); // i.e. Random
}
