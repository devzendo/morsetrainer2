package org.devzendo.morsetrainer2.source;

import java.util.Optional;

public class Source {

	public static enum PlayType {
		Callsigns,
		QSO,
		File,
		Stdin,
		Text,
		Codes;

		public static Optional<PlayType> fromString(final String anyString) {
			if (anyString == null) {
				return Optional.empty();
			}
			final String lowerCase = anyString.toLowerCase();
			for (final PlayType value : PlayType.values()) {
				if (value.name().toLowerCase().equals(lowerCase)) {
					return Optional.of(value);
				}
			}
			if (anyString.equals("-")) {
				return Optional.of(Stdin);
			}
			return Optional.empty();
		}
	}

	public static enum SourceType {
		All(LETTERS + NUMBERS + PUNCTUATION + PROSIGNS),
		Letters(LETTERS),
		Numbers(NUMBERS),
		Punctuation(PUNCTUATION),
		Prosigns(PROSIGNS),
		Set("");

		private final String content;
		private SourceType(final String content) {
			this.content = content;
		}

		public static Optional<SourceType> fromString(final String anyString) {
			if (anyString == null) {
				return Optional.empty();
			}
			final String lowerCase = anyString.toLowerCase();
			for (final SourceType value : SourceType.values()) {
				if (value.name().toLowerCase().equals(lowerCase)) {
					return Optional.of(value);
				}
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
