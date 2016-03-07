package org.devzendo.morsetrainer2.prefs;

import java.io.File;

import org.devzendo.commoncode.string.StringUtils;

public class PrefsFactory {
	private final File absolutePrefsDir;
	private final File absolutePrefsFile;

	public PrefsFactory(final File homeDir, final String prefsSubDir, final String prefsFile) {
        absolutePrefsDir = new File(StringUtils.slashTerminate(homeDir.getAbsolutePath()) + prefsSubDir);
        absolutePrefsFile = new File(StringUtils.slashTerminate(absolutePrefsDir.getAbsolutePath()) + prefsFile);
	}

	public PrefsFactory(final String prefsSubDir, final String prefsFile) {
		this(new File(System.getProperty("user.home")), prefsSubDir, prefsFile);
	}

    public boolean prefsDirectoryExists() {
        return absolutePrefsDir.exists();
    }

    public boolean createPrefsDirectory() {
        return absolutePrefsDir.mkdir();
    }

    public File getPrefsDir() {
        return absolutePrefsDir;
    }

    public File getPrefsFile() {
        return absolutePrefsFile;
    }
}
