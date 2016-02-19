package org.devzendo.morsetrainer2.symbol;

import org.apache.commons.lang3.tuple.Pair;

public class PartyMorseCharacter extends Pair<Integer, MorseCharacter> {

	private static final long serialVersionUID = 1L;
	private Integer number;
	private MorseCharacter character;

	public PartyMorseCharacter(final Integer number, final MorseCharacter character) {
		super();
		this.number = number;
		this.character = character;
	}
	@Override
	public MorseCharacter setValue(MorseCharacter value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer getLeft() {
		return number;
	}

	@Override
	public MorseCharacter getRight() {
		return character;
	}
}
