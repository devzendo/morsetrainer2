package org.devzendo.morsetrainer2.source;

import java.util.Optional;

public class Source {

	public static enum SourceType {
		All(LETTERS + NUMBERS + PUNCTUATION + PROSIGNS),
		Letters(LETTERS),
		Numbers(NUMBERS),
		Punctuation(PUNCTUATION),
		Prosigns(PROSIGNS),
		Callsigns(""),
		QSO(""),
		Set(""),
		File(""),
		Stdin(""),
		Text("");
	
		private final String content;
		private SourceType(final String content) {
			this.content = content;
		}
		
		public static Optional<SourceType> fromString(final String anyString) {
			if (anyString == null) {
				return Optional.empty();
			}
			final String lowerCase = anyString.toLowerCase();
			for (SourceType value : SourceType.values()) {
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

	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String NUMBERS = "0123456789";
	private static final String PUNCTUATION = ",./?+=";
	private static final String PROSIGNS = "<AR><AS><BK><BT><CL><CQ><HH><KA><KN><NR><SK><VE>";
}
