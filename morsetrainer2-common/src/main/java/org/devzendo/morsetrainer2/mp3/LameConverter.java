package org.devzendo.morsetrainer2.mp3;

import java.io.File;
import java.io.IOException;

import org.devzendo.commoncode.executor.IteratorExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LameConverter implements Mp3Converter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LameConverter.class);

	@Override
	public boolean converterAvailable() {
		final IteratorExecutor executor = new IteratorExecutor(new String[] {"lame", "--version" });
		boolean found = false;
		while (executor.hasNext()) {
			final String line = (String) executor.next();
			LOGGER.debug("Line: [" + line + "]");
			if (line.matches("^LAME .*version [\\d\\.]+ \\(http://.*$" /* etc */)) {
				LOGGER.debug("Found version line");
				found = true;
			}
		}
		final IOException except = executor.getIOException();
		if (except != null) {
			LOGGER.debug("Exception on running lame: " + except.getMessage());
			found = false;
		}
		return found;
	}

	@Override
	public File convertToMP3(final File wavFile) {
		final File mp3File = Conversion.toMP3(wavFile);
		final IteratorExecutor executor = new IteratorExecutor(new String[] {"lame", "--quiet", wavFile.getAbsolutePath(), mp3File.getAbsolutePath() });
		// shouldn't be any output...
		while (executor.hasNext()) {
			final String line = (String) executor.next();
			LOGGER.debug("Line: [" + line + "]");
		}
		final IOException except = executor.getIOException();
		if (except != null) {
			final String msg = "Exception on converting with lame: " + except.getMessage();
			LOGGER.debug(msg);
			throw new RuntimeException(msg);
		}
		if (mp3File.length() == 0L) {
			final String msg = "Converted file " + mp3File.getAbsolutePath() + " has zero length";
			LOGGER.debug(msg);
			throw new RuntimeException(msg);
		}
		return mp3File;
	}
}
