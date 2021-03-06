package com.autocomplete;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class WordsLoader {

    private static final String WORD_REGEX = "[^a-z]+";

    public String[] loadFromFile(String path) {
        StringBuilder words = new StringBuilder();
        try {
            collectWords(path, words);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return Pattern.compile(WORD_REGEX).split(words);
    }

    private void collectWords(String path, StringBuilder words) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(new File(path)));
        String nextLine;
        while ((nextLine = in.readLine()) != null) {
            words.append(nextLine);
        }
    }
}