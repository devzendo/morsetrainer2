package org.devzendo.morsetrainer2.editmatcher;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.devzendo.commoncode.collection.Lists.join;
import static org.devzendo.morsetrainer2.editmatcher.Edit.deletion;
import static org.devzendo.morsetrainer2.editmatcher.Edit.match;
import static org.devzendo.morsetrainer2.editmatcher.Edit.mutation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Levenshtein distance calculator, that also returns the list of edits. With
 * gratitude to Matt Malone, whose
 * http://oldfashionedsoftware.com/2009/11/19/string-distance-and-refactoring-in-scala/
 * was a great help in improving my understanding of the algorithm.
 */
public class EditMatcher<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(EditMatcher.class);

	private final DistEdit<T>[][] dist;
	private final T[] s1;
	private final T[] s2;

	private final DistEdit<T> finalCell;
	private final List<Edit<T>> edits;
	private final int distance;

	private static class DistEdit<T> {
		protected final int dist;
		protected final List<Edit<T>> edits;

		public DistEdit(final int dist, final List<Edit<T>> edits) {
			this.dist = dist;
			this.edits = edits;
		}
	};

	public EditMatcher(final T[] s1, final T[] s2) {
        this.s1 = s1;
		this.s2 = s2;
		dist = new DistEdit[s1.length + 1][s2.length + 1];
        for (int x = 0; x < s1.length + 1; x++) {
            for (int y = 0; y < s2.length + 1; y++) {
            	dist[x][y] = new DistEdit<T>(0, emptyList());
            }
        }

        for (int x = 1; x < s1.length + 1; x++) {
        	dist[x][0] = new DistEdit<T>(x, asList(deletion(s1[x-1])));
        }
        for (int y = 1; y < s2.length + 1; y++) {
        	dist[0][y] = new DistEdit<T>(y, asList(deletion(s2[y-1])));
        }

        /*
		        t a p e
		      0 1 2 3 4
		    h 1 1 2 3 4
		    a 2 2 1 2 3
		    t 3 2 2 2 3
		 */

        for (int x = 1; x < s1.length + 1; x++) {
            for (int y = 1; y < s2.length + 1; y++) {
            	final DistEdit<T> deS1 = dist[x-1][y];
            	@SuppressWarnings("unchecked")
				final DistEdit<T> delFromS1 = new DistEdit<T>(deS1.dist + 1, join(deS1.edits, asList(deletion(s1[x-1]))));

            	final DistEdit<T> deS2 = dist[x][y - 1];
            	@SuppressWarnings("unchecked")
				final DistEdit<T> delFromS2 = new DistEdit<T>(deS2.dist + 1, join(deS2.edits, asList(deletion(s2[y-1]))));

            	final DistEdit<T> deS1S2 = dist[x - 1][y - 1];
            	@SuppressWarnings("unchecked")
				final DistEdit<T> delFromS1AndS2 = (s1[x - 1].equals(s2[y - 1])) ?
            			new DistEdit<T>(deS1S2.dist + 0, join(deS1S2.edits, asList(match(s1[x - 1])))) :
        				new DistEdit<T>(deS1S2.dist + 1, join(deS1S2.edits, asList(mutation(s1[x - 1]))));

    			dist[x][y] = minimum(delFromS1, delFromS2, delFromS1AndS2);
            }
        }

        finalCell = dist[s1.length][s2.length];
        edits = finalCell.edits;
        distance = finalCell.dist;
	}

	public void display() {
		LOGGER.info("H s1: '" + s1 + "' width " + s1.length);
        LOGGER.info("V s2: '" + s2 + "' height " + s2.length);

		final StringBuilder sb = new StringBuilder();
        sb.append("      ");
        for(final T ch : s1) {
        	sb.append(String.format("%3s ", ch));
        }
        LOGGER.info(sb.toString());
        for (int y = 0; y < s2.length + 1; y++) {
            final StringBuilder sby = new StringBuilder();
            if (y == 0) {
            	sby.append("  ");
            } else {
            	sby.append(s2[y - 1] + " ");
            }
            for (int x = 0; x < s1.length + 1; x++) {
            	sby.append(String.format("%3d ", dist[x][y].dist));
            }
            LOGGER.info(sby.toString());
        }
        LOGGER.info("Levenshtein distance of '" + s1 + "' to '" + s2 + "' is " + distance);
        LOGGER.info("Edits are: " + edits);
	}

    public List<Edit<T>> edits() {
		return edits;
	}

	public int distance() {
		return distance;
	}

	private DistEdit<T> minimum(final DistEdit<T> i1, final DistEdit<T> i2, final DistEdit<T> i3) {
        if (i1.dist < i2.dist) {
            if (i1.dist < i3.dist) {
                return i1;
            } else {
                return i3;
            }
        } else {
            // i1 >= i2
            if (i2.dist < i3.dist) {
                return i2;
            } else {
                return i3;
            }
        }
    }
}
