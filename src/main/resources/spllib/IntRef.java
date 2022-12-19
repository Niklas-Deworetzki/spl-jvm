/**
 * SPL integer type with support for by-reference semantics.
 */
public final class IntRef {
    public int value;

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IntRef ref && this.value == ref.value;
    }
}
