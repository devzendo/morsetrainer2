package org.devzendo.morsetrainer2.editmatcher;

public class Edit <T> {
	enum Type { Deletion, Match, Mutation };

	public static <T> Edit<T> deletion(final T ch) {
		return new Edit<T>(ch, Type.Deletion);
	}

	public static <T> Edit<T> match(final T ch) {
		return new Edit<T>(ch, Type.Match);
	}

	public static <T> Edit<T> mutation(final T ch) {
		return new Edit<T>(ch, Type.Mutation);
	}

	protected final T ch;
	protected final Type type;

	public Edit(final T ch, final Type type) {
		this.ch = ch;
		this.type = type;
	}

	@Override
	public String toString() {
		return type.name() + '(' + ch + ')';
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edit<?> other = (Edit<?>) obj;
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return (ch == other.ch);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((ch == null) ? 0 : ch.hashCode());
        return result;
	}
}