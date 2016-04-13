package org.devzendo.morsetrainer2.mp3;

import java.io.File;
import java.util.Optional;

public class Conversion {
	public static Optional<File> toWav(final Optional<File> recordFile) {
		if (!recordFile.isPresent()) {
			return Optional.empty();
		}
		final File file = recordFile.get();
		return Optional.of(new File(file.getParentFile(), file.getName().replaceFirst("\\.mp3$", ".wav")));
	}

	public static Optional<File> toMP3(final Optional<File> recordFile) {
		if (!recordFile.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(toMP3(recordFile.get()));
	}

	public static File toMP3(final File file) {
		return new File(file.getParentFile(), file.getName().replaceFirst("\\.wav$", ".mp3"));
	}

	public static boolean isWav(final File file) {
		return file.getName().matches("^.*\\.wav$");
	}

	public static boolean isMP3(final File file) {
		return file.getName().matches("^.*\\.mp3$");
	}

	public static boolean isWav(final Optional<File> file) {
		if (file.isPresent()) {
			return isWav(file.get());
		}
		return false;
	}

	public static boolean isMP3(final Optional<File> file) {
		if (file.isPresent()) {
			return isMP3(file.get());
		}
		return false;
	}

}
