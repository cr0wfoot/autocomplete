package com.autocomplete;

import com.autocomplete.storage.RWayTrie;

import java.util.Iterator;

public class Run {

    private static final String PATH_TO_FILE = "resources/words-333333.txt";

    public static void main( String[] args ) {
        PrefixMatcher prefixMatcher = new PrefixMatcher(new RWayTrie<Integer>());
        prefixMatcher.add(new WordsLoader().loadFromFile(PATH_TO_FILE));
        System.out.println(prefixMatcher.size());

        String prefix = "the";
        int depth = 4;

        for (Iterator<String> iterator = prefixMatcher.wordsWithPrefix(prefix, depth).iterator(); iterator.hasNext();)
            System.out.println(iterator.next());
    }
}
