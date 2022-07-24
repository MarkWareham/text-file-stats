package name.markwareham.textfilestats;

import java.util.Objects;

/**
 * Represents a count of words of the specified length.
 * For example 102 words that are 4 characters long.
 *
 */
public final class WordLengthCount {

    private int wordLength;
    private Long occurences;

    protected WordLengthCount(final int wordLength) {
        this.wordLength = wordLength;
        this.occurences = 0L;
    }

    protected WordLengthCount(final int wordLength, final Long occurences) {
        this.wordLength = wordLength;
        this.occurences = occurences;
    }

    /**
     * The word length this object is representing.
     * @return integer of the count of the number of characters in the word
     */
    public int getWordLength() {
        return wordLength;
    }

    /**
     * Retrieves the number of times this word length occurs.
     * @return number of times this word length occurs
     */
    public Long getOccurences() {
        return occurences;
    }

    protected void increment() {
        occurences++;
    }

    @Override
    public String toString() {
        return String.format("Number of words of length %s is %s", wordLength, occurences);
    }

    @Override
    public int hashCode() {
        return Objects.hash(occurences, wordLength);
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        WordLengthCount other = (WordLengthCount) object;
        return Objects.equals(occurences, other.occurences) && wordLength == other.wordLength;
    }
}
