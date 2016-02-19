package org.devzendo.morsetrainer2.cmd;

import java.io.File;
import java.util.Optional;

public class Options {
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String NUMBERS = "0123456789";
	private static final String PUNCTUATION = ",./?+=";
	private static final String PROSIGNS = "<AR><AS><BK><BT><CL><CQ><HH><KA><KN><NR><SK><VE>";
	public static enum Source {
		All(LETTERS + NUMBERS + PUNCTUATION + PROSIGNS),
		Letters(LETTERS),
		Numbers(NUMBERS),
		Punctuation(PUNCTUATION),
		Prosigns(PROSIGNS),
		Callsigns(""),
		QSO(""),
		Set(""),
		File(""),
		Stdin("");

		private final String content;
		private Source(final String content) {
			this.content = content;
		}
		
		public static Optional<Source> fromString(final String anyString) {
			if (anyString == null) {
				return Optional.empty();
			}
			final String lowerCase = anyString.toLowerCase();
			for (Source value : Source.values()) {
				if (value.name().toLowerCase().equals(lowerCase)) {
					return Optional.of(value);
				}
			}
			if (anyString.equals("-")) {
				return Optional.of(Stdin);
			}
			return Optional.empty();
		}

		public String content() {
			return content;
		}
	}
	
	public Integer wpm;
	public Integer fwpm;
	public Integer freqHz;
	
	public Source source = Options.Source.All;
	public String sourceString = source.content();

	public boolean interactive = false;
	public Optional<File> recordFile = Optional.empty();
	public Optional<Integer> length = Optional.empty(); // i.e. Random
}
