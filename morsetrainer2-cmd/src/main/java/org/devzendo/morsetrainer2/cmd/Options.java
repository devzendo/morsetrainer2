package org.devzendo.morsetrainer2.cmd;

import java.io.File;
import java.util.Optional;

import org.devzendo.morsetrainer2.source.Source;

public class Options {
	public Integer wpm;
	public Integer fwpm;
	public Integer freqHz;
	
	public Source.SourceType source = Source.SourceType.All;
	public String sourceString = source.content();

	public boolean interactive = false;
	public Optional<File> recordFile = Optional.empty();
	public Optional<Integer> length = Optional.empty(); // i.e. Random
}
