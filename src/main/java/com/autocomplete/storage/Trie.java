package com.autocomplete.storage;

public interface Trie<T> {

    void add(String word, T value);

    int size();

    boolean contains(String word);

    Iterable<String> words();

    Iterable<String> wordsWithPrefix(String pref);

    boolean delete(String word);
}