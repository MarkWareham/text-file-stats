package name.markwareham.textfilestats;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * API for reporting on word statistics within a text file.
 *
 * A word is any contiguous block of characters whether alphabetical, numerical, or special
 * characters, or any combination of the three. Separated by a space. Punctuation at the end
 * of a word is not included in it's character count.
 */
public class WordStats {

    private static final Logger LOG = Logger.getLogger(WordStats.class.getName());
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final int SCALE = 3;

    private File inputFile;

    // Seems odd that the map key is also stored in the wordLength Counts, but
    // that's because it's needed by key for the counting of each length and by the
    // WordLengthCount Object in modal calculation
    private Map<Integer, WordLengthCount> wordLengthCounts = new HashMap<>();

    private Long wordCount = 0L;
    private BigDecimal averageWordLength;
    private List<WordLengthCount> modalLength;

    // used for average calculation
    private BigDecimal charsInWordsCount = BigDecimal.ZERO;

    /**
     * Constructs a new instance of WordStats with the specified file as the source
     * document.
     *
     * @param inputFile the file that statistics are to be reported on
     */
    public WordStats(final File inputFile) throws FileNotFoundException {
        if (inputFile == null) {
            throw new IllegalArgumentException("inputFile cannot be null");
        }
        this.inputFile = inputFile;
        loadStats();
    }

    private void loadStats() throws FileNotFoundException {

        LOG.log(Level.FINE, "Loading stats from file ", inputFile);

        Scanner scanner = new Scanner(inputFile);
        // default delimter is space

        String word;
        int wordLength;

        while (scanner.hasNext()) {
            word = withoutPunctuation(scanner.next());

            wordLength = word.length();

            incrementLengthCount(wordLength);
            wordCount++;
            charsInWordsCount = charsInWordsCount.add(BigDecimal.valueOf(wordLength));

        }
        calculateAverageWordLength();
        calculateModalLength();
        scanner.close();

    }

    private void calculateModalLength() {
        long maxWordlengthCount = 0;

        // find the max
        for (WordLengthCount wordLengthCount : wordLengthCounts.values()) {
            if (wordLengthCount.getOccurences() > maxWordlengthCount) {
                maxWordlengthCount = wordLengthCount.getOccurences();
            }

        }

        // add all occurrences of the max to return object
        List<WordLengthCount> modals = new ArrayList<>();
        for (WordLengthCount wordLengthCount : wordLengthCounts.values()) {
            if (wordLengthCount.getOccurences() == maxWordlengthCount) {
                modals.add(wordLengthCount);
            }
        }
        modalLength = modals;
    }

    private String withoutPunctuation(final String word) {
        char lastChar = word.charAt(word.length() - 1);
        if (lastChar == '.' || lastChar == ',') {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }

    private void calculateAverageWordLength() {
        averageWordLength = charsInWordsCount.divide(BigDecimal.valueOf(wordCount), SCALE, ROUNDING_MODE);
    }

    private void incrementLengthCount(final int wordLength) {
        WordLengthCount entry = wordLengthCounts.get(wordLength);
        if (entry == null) {
            // we haven't yet counted a word of this length. So create an entry
            wordLengthCounts.put(wordLength, new WordLengthCount(wordLength));
        }
        wordLengthCounts.get(wordLength).increment();
    }

    /**
     * Returns the number of words in the file.
     *
     * A word is any contiguous block of characters whether alphabetical, numerical, or special
     * characters, or any combination of the three. Separated by a space. Punctuation at the end
     * of a word is not included in it's character count.
     *
     * @return the total number of words in the file
     */
    public Long numberOfWords() {
        return wordCount;
    }

    /**
     * With a precision of 3 decimal places, provides the average length of all
     * words.
     *
     * A word is any contiguous block of characters whether alphabetical, numerical, or special
     * characters, or any combination of the three. Separated by a space. Punctuation at the end
     * of a word is not included in it's character count.
     *
     * @return the average length of all words
     */
    public BigDecimal averageWordLength() {
        return averageWordLength;
    }

    /**
     * Retrieves the modal (most frequently occurring) length of words in the file.
     * There could be more than one modal!
     *
     * A word is any contiguous block of characters whether alphabetical, numerical, or special
     * characters, or any combination of the three. Separated by a space. Punctuation at the end
     * of a word is not included in it's character count.
     *
     * @return the modal (most frequently occurring) length of words in the file
     */
    public List<WordLengthCount> modalLength() {
        return modalLength;
    }

    /**
     * Counts the number of words of each length in the file.
     *
     * A word is any contiguous block of characters whether alphabetical, numerical, or special
     * characters, or any combination of the three. Separated by a space. Punctuation at the end
     * of a word is not included in it's character count.
     *
     * @return number of occurrences of each length of word
     */
    public List<WordLengthCount> listOccurencesOfEachLength() {
        return List.copyOf(wordLengthCounts.values());
    }

    /**
     * Produces the statistics report for the provided file.
     *
     * @return String representation of the report.
     */
    public String produceReport() {

        // String builder rather than string.format() for performance over readability.
        StringBuilder builder = new StringBuilder();
        builder.append("Word count = ").append(wordCount).append(System.lineSeparator())
                .append("Average word length = ").append(averageWordLength).append(System.lineSeparator());
        for (WordLengthCount wordLengthCount : listOccurencesOfEachLength()) {
            builder.append(wordLengthCount).append(System.lineSeparator());
        }
        builder.append("The most frequently occurring word length is ").append(modalLength.get(0).getOccurences())
                .append(", for word length");
        if (modalLength.size() > 1) {
            builder.append("s");
        }
        builder.append(" of ");
        builder.append(modalLength.get(0).getWordLength());
        modalLength.stream().skip(1).forEach(modal -> builder.append(" & " + modal.getWordLength()));
        return builder.toString();
    }
}
