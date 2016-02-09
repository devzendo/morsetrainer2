package org.devzendo.morsetrainer2.xlat;

import java.util.List;

import org.devzendo.morsetrainer2.sound.ClipGenerator;
import org.devzendo.morsetrainer2.sound.ClipPlayer;
import org.devzendo.morsetrainer2.symbol.Pulse;

public class PulsePlayer {
	private ClipPlayer player;
	private ClipGenerator clipGen;

	public PulsePlayer(final ClipGenerator clipGen, final ClipPlayer player) {
		this.clipGen = clipGen;
		this.player = player;
	}
	
	public void play(final List<Pulse> pulses) throws InterruptedException {
		for (Pulse pulse : pulses) {
			player.playSynchronously(clipGen.translate(pulse));
		}
	}
}
