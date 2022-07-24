package name.markwareham.textfilestats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class WordStatsTest {

    private WordStats wordStats;

    @BeforeAll
    private void init() {
        try {
            //Paths file system separator independent
            wordStats = new WordStats(Paths.get("src", "test", "resources", "example.txt").toFile());
        } catch (FileNotFoundException e) {
            fail("Couldn't find the example file through classloader resource", e);
        }
    }

    @Test
    void testConstructionFileParameterNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new WordStats(null);
        });
    }

    @Test
    void testConstructionFileParamaterNotExists() {
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            new WordStats(new File("file/that/doesn't/exist"));
        });
    }

    @Test
    void testNumberOfWords() {
        // uses src/test/resources/example.txt contents as test data
        assertEquals(9, wordStats.numberOfWords(), "number of words inccorect");
    }

    @Test
    void testAverageWordLength() {
        // uses src/test/resources/example.txt contents as test data
        BigDecimal expected = BigDecimal.valueOf(4.556);
        assertEquals(expected, wordStats.averageWordLength(), "average length of words inccorect");
    }

    @Test
    void testModalLength() {

        var expected1 = new WordLengthCount(4, 2L);
        var expected2 = new WordLengthCount(5, 2L);
        var actual = wordStats.modalLength();
        assertNotNull(actual);
        assertEquals(2, actual.size(), "results size incorrect");
        assertEquals(expected1, actual.get(0), "most frequently occuring length first result inccorect");
        assertEquals(expected2, actual.get(1), "most frequently occuring length second result inccorect");
    }

    @Test
    void testListOccurencesOfEachLength() {
        Collection<WordLengthCount> expected = new ArrayList<>();
        expected.add(new WordLengthCount(1, 1L));
        expected.add(new WordLengthCount(2, 1L));
        expected.add(new WordLengthCount(3, 1L));
        expected.add(new WordLengthCount(4, 2L));
        expected.add(new WordLengthCount(5, 2L));
        expected.add(new WordLengthCount(7, 1L));
        expected.add(new WordLengthCount(10, 1L));

        Collection<WordLengthCount> actual = wordStats.listOccurencesOfEachLength();
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size(), "results size incorrect");
        assertEquals(expected, actual);
    }

    @Test
    void fullReport() {
        String expected = "Word count = 9" + System.lineSeparator()
                + "Average word length = 4.556" + System.lineSeparator()
                + "Number of words of length 1 is 1" + System.lineSeparator()
                + "Number of words of length 2 is 1" + System.lineSeparator()
                + "Number of words of length 3 is 1" + System.lineSeparator()
                + "Number of words of length 4 is 2" + System.lineSeparator()
                + "Number of words of length 5 is 2" + System.lineSeparator()
                + "Number of words of length 7 is 1" + System.lineSeparator()
                + "Number of words of length 10 is 1" + System.lineSeparator()
                + "The most frequently occurring word length is 2, for word lengths of 4 & 5";

        assertEquals(expected, wordStats.produceReport());
    }

}
