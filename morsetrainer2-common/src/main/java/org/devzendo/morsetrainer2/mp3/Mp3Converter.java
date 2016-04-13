package org.devzendo.morsetrainer2.mp3;

import java.io.File;

public interface Mp3Converter {
	boolean converterAvailable();

	File convertToMP3(File wavFile);
}
