package org.devzendo.morsetrainer2.stats;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class WordLengthPerformance {
	private final int length;
	private final double accuracyPercentage;
	private final LocalDateTime when;

	public WordLengthPerformance(final int length, final LocalDateTime when, final double accuracyPercentage) {
		this.length = length;
		this.when = when;
		this.accuracyPercentage = accuracyPercentage;
	}

	public int getWordLength() {
		return length;
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
