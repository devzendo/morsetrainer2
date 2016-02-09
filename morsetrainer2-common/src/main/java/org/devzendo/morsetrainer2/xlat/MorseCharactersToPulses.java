package org.devzendo.morsetrainer2.xlat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.devzendo.morsetrainer2.symbol.MorseCharacter;
import org.devzendo.morsetrainer2.symbol.Pulse;

public class MorseCharactersToPulses {

	public MorseCharactersToPulses() {
		
	}
	
	public List<Pulse> translate(final List<MorseCharacter> input) {
		final Iterator<MorseCharacter> it = input.iterator();
		boolean lastWasChar = false;
		final List<Pulse> out = new ArrayList<>();
		
		while (it.hasNext()) {
			
			final MorseCharacter mc = it.next();
			final boolean isSpace = mc.equals(MorseCharacter.SPC);

			// Add Pulse.ch after the last char? But not if this is a SPC
			if (lastWasChar && !isSpace) {
				out.add(Pulse.ch);
			}
			
			// Add each pulse from the char, interspersed with element pulses.
			// NB: SPC has NO PULSES, so nothing is added for SPC.
			final Pulse[] pulses = mc.getPulses();
			for (int i=0; i<pulses.length; i++) {
				out.add(pulses[i]);
				if (i != pulses.length - 1) {
					out.add(Pulse.el);
				}
			}
			
			if (isSpace) {
				out.add(Pulse.wo);
				lastWasChar = false;
			} else {
				lastWasChar = true; 
				// so the Pulse.ch will be added before the NEXT char (if there is one), since THIS char might be the last.
			}
		}
		return out;
	}
}
