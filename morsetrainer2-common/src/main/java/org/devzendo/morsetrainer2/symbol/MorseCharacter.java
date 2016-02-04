package org.devzendo.morsetrainer2.symbol;

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
	static {
		for (MorseCharacter mc: MorseCharacter.values()) {
			stringToMorseCharacter.put(mc.toString(), mc);
		}
	};

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
		final MorseCharacter morseCharacter = stringToMorseCharacter.get(trimmed);
		if (morseCharacter == null) {
			return Optional.empty();
		}
		return Optional.of(morseCharacter);
	}

}
