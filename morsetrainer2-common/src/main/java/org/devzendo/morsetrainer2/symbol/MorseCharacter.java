package org.devzendo.morsetrainer2.symbol;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum MorseCharacter {
	SPC(" ", ""),
	A(".-"),
	B("-..."),
	C("-.-."),
	D("-.."),
	E("."),
	F("..-."),
	G("--."),
	H("...."),
	I(".."),
	J(".---"),
	K("-.-"),
	L(".-.."),
	M("--"),
	N("-."),
	O("---"),
	P(".--."),
	Q("--.-"),
	R(".-."),
	S("..."),
	T("-"),
	U("..-"),
	V("...-"),
	W(".--"),
	X("-..-"),
	Y("-.--"),
	Z("--.."),
	D0("0", "-----"),
	D1("1", ".----"),
	D2("2", "..---"),
	D3("3", "...--"),
	D4("4", "....-"),
	D5("5", "....."),
	D6("6", "-...."),
	D7("7", "--..."),
	D8("8", "---.."),
	D9("9", "----."),
	AAA(".", ".-.-.-"),
	AR("<AR>", ".-.-."),
	AS("<AS>", ".-..."),
	BK("<BK>", "-...-.-"),
	BT("<BT>", "-...-"),
	CL("<CL>", "-.-..-.."),
	CQ("<CQ>", "-.-.--.-"),
	HH("<HH>", "........"),
	IMI("<IMI>", "..--.."),
	KA("<KA>", "-.-.-"),
	KN("<KN>", "-.--."),
	NR("<NR>", "-..-."),
	SK("<SK>", "...-.-"),
	VE("<VE>", "...-."),
	EQUAL("=", "-...-"),
	PLUS("+", ".-.-."),
	COMMA(",", "--..--"),
	QUESTION("?", "..--.."),
	SLASH("/", "-..-."),
	FULLSTOP(".", ".-.-.-");

	private static final Map<String, MorseCharacter> stringToMorseCharacter = new HashMap<>();
	private static final Map<Character, MorseCharacter> charToMorseCharacter = new HashMap<>();
	private static final Map<String, MorseCharacter> prosignTextToMorseCharacter = new HashMap<>();

	static {
		for (final MorseCharacter mc: MorseCharacter.values()) {
			final String mcToString = mc.toString();

			stringToMorseCharacter.put(mcToString, mc);

			if (mcToString.length() == 1) {
				charToMorseCharacter.put(new Character(mcToString.charAt(0)), mc);
			}

			if (mcToString.charAt(0) == '<') {
				// 0 1 2 3 length 4
				// < K N >
				final String prosignText = mcToString.substring(1, mcToString.length() - 1);
				prosignTextToMorseCharacter.put(prosignText, mc);
			}
		}
	};

	public static String arrayToString(final MorseCharacter ... mcs) {
		return asList(mcs).stream().map(mc -> mc.toString()).collect(joining());
	}

	public static MorseCharacter[] allocate(final int n) {
		return new MorseCharacter[n];
	}

	private final String display;
	private final Pulse[] pulses;

	private MorseCharacter(final String dotDashRep) {
		pulses = toPulses(dotDashRep);
		display = name();
	}

	private MorseCharacter(final String toDisplay, final String dotDashRep) {
		display = toDisplay;
		pulses = toPulses(dotDashRep);
	}

	private Pulse[] toPulses(final String dotDashRep) {
		final Pulse[] out = new Pulse[dotDashRep.length()];
		for (int i=0; i<dotDashRep.length(); i++) {
			out[i] = (dotDashRep.charAt(i) == '.') ? Pulse.dit : Pulse.dah;
		}
		return out;
	}

	@Override
	public String toString() {
		return display;
	}

	public Pulse[] getPulses() {
		return pulses;
	}

	public static Optional<MorseCharacter> fromString(final String string) {
		if (string == null) {
			return Optional.empty();
		}
		if (string.equals(" ")) {
			return Optional.of(SPC);
		}
		final String trimmed = string.trim().toUpperCase();
		if (trimmed.length() == 0) {
			return Optional.empty();
		}
		return Optional.ofNullable(stringToMorseCharacter.get(trimmed));
	}

	public static Optional<MorseCharacter> fromChar(final char ch) {
		return Optional.<MorseCharacter>ofNullable(charToMorseCharacter.get(Character.toUpperCase(ch)));
	}

	public static Optional<MorseCharacter> fromProsignText(final String string) {
		if (string == null) {
			return Optional.empty();
		}
		return Optional.<MorseCharacter>ofNullable(prosignTextToMorseCharacter.get(string.toUpperCase()));
	}
}
