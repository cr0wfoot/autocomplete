package com.autocomplete;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WordsLoaderTest {

    private static final String PATH_TO_FILE = "resources/words-333333.txt";

    private WordsLoader instance = new WordsLoader();

    @Test
    public void shouldLoadAllWordsFromFile() {
        int expectedNumberOfWords = 333334;

        int loadedNumberOfWords = instance.loadFromFile(PATH_TO_FILE).length;

        assertEquals(expectedNumberOfWords, loadedNumberOfWords);
    }
}
