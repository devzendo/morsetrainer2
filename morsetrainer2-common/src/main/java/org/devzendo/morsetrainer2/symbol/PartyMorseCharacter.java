package org.devzendo.morsetrainer2.symbol;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PartyMorseCharacter {

	private final Integer number;
	private final MorseCharacter character;

	public PartyMorseCharacter(final Integer number, final MorseCharacter character) {
		super();
		this.number = number;
		this.character = character;
	}

	public Integer getLeft() {
		return number;
	}

	public MorseCharacter getRight() {
		return character;
	}

	@Override
	public String toString() {
		return "(" + number + ", " + character.toString() + ")";
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(obj, this);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
