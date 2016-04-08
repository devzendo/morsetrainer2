package org.devzendo.morsetrainer2.stats;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.devzendo.morsetrainer2.symbol.MorseCharacter;

public class MorseCharacterPerformance {
	private final MorseCharacter mc;
	private final double accuracyPercentage;
	private final LocalDateTime when;

	public MorseCharacterPerformance(final MorseCharacter mc, final LocalDateTime when, final double accuracyPercentage) {
		this.mc = mc;
		this.when = when;
		this.accuracyPercentage = accuracyPercentage;
	}

	public MorseCharacter getMorseCharacter() {
		return mc;
	}

	public LocalDateTime getWhen() {
		return when;
	}

	public double getAccuracyPercentage() {
		return accuracyPercentage;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}
}
