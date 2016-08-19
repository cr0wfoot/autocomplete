package com.autocomplete;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class WordsLoader {

    private static final String WORD_REGEX = "[^a-z]+";

    public String[] loadFromFile(String path) {
        StringBuilder words = new StringBuilder();
        try {
            collectWords(path, words);
        } catch (IOException ex) {
            Logger.getLogger(WordsLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Pattern.compile(WORD_REGEX).split(words);
    }

    private void collectWords(String path, StringBuilder words) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(new File(path)));
        String tmp;
        while ((tmp = in.readLine()) != null)
            words.append(tmp);
    }
}