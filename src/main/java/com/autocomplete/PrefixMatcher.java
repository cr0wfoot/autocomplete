package com.autocomplete;

import com.autocomplete.storage.Trie;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class PrefixMatcher {

    private static final int MIN_WORD_SIZE = 2;
    private static final String WORDS_SEPARATOR = " ";

    private Trie trie;

    public PrefixMatcher(Trie trie) {
        this.trie = trie;
    }

    public boolean delete(String word) {
        return isWordValid(word) && trie.delete(word);
    }

    public boolean contains(String word) {
        return isWordValid(word) && trie.contains(word);
    }

    private boolean isWordValid(String word) {
        return word != null && word.length() > MIN_WORD_SIZE;
    }

    public int size() {
        return trie.size();
    }

    public int add(String... strings) {
        int wordsAdded = 0;
        if (strings != null && strings.length > 0)
            for (String str : strings)
                wordsAdded += handleString(str);
        return wordsAdded;
    }

    private int handleString(String str) {
        int wordsAdded = 0;
        if (str != null)
            wordsAdded += addWordsToTrie(str.split(WORDS_SEPARATOR));
        return wordsAdded;
    }

    private int addWordsToTrie(String[] words) {
        int wordsAdded = 0;
        for (String word : words)
            if (isWordValid(word)) {
                trie.add(word.toLowerCase(), word.length());
                wordsAdded++;
            }
        return wordsAdded;
    }

    public Iterable<String> wordsWithPrefix(String prefix) {
        if (isPrefixNotValid(prefix))
            return new ArrayDeque();
        return trie.wordsWithPrefix(prefix);
    }

    private boolean isPrefixNotValid(String prefix) {
        return prefix == null || prefix.length() < MIN_WORD_SIZE;
    }

    public Iterable<String> wordsWithPrefix(final String prefix, final int depth) {
        if (isPrefixNotValid(prefix))
            return new ArrayDeque();
        if (depth < 0)
            throw new IllegalArgumentException();
        return () -> new Iterator<String>() {

            private static final int PAIR_OF_ELEMENTS = 2;
            private Iterator<String> trieIterator = trie.wordsWithPrefix(prefix).iterator();
            private Queue<String> element = new ArrayDeque<>(PAIR_OF_ELEMENTS);
            private String currentElement;

            {
                tryToAddElement();
            }

            private void tryToAddElement() {
                if (trieIterator.hasNext()) {
                    String elementFromTrie = trieIterator.next();
                    if (isElementAppliesDepth(elementFromTrie))
                        element.add(elementFromTrie);
                }
            }

            private boolean isElementAppliesDepth(String elementFromTrie) {
                return elementFromTrie.length() <= depth + MIN_WORD_SIZE;
            }

            public boolean hasNext() {
                return !element.isEmpty();
            }

            public String next() {
                if (element.isEmpty())
                    throw new NoSuchElementException();
                tryToAddElement();
                currentElement = element.poll();
                return currentElement;
            }

            public void remove() {
                boolean elementNotRemoved = !trie.delete(currentElement);
                if (elementNotRemoved)
                    throw new IllegalStateException();
                currentElement = null;
            }
        };
    }
}