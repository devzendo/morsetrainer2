package org.devzendo.morsetrainer2.cmd;

import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiRenderer;

public class AnsiHelper {
	public static void print(final String str) {
		AnsiConsole.out().print(AnsiRenderer.render(str));
	}

	public static void println(final String str) {
		AnsiConsole.out().println(AnsiRenderer.render(str));
	}

	public static void printraw(final String str) {
		AnsiConsole.out().print(str);
	}

	public static void printlnraw(final String str) {
		AnsiConsole.out().println(str);
	}

	public static void flush() {
		AnsiConsole.out().flush();
	}
}
