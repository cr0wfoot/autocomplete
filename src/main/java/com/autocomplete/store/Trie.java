package com.autocomplete.store;

public interface Trie<T> {

    public void add(String word, T value);

    public int size();

    public boolean contains(String word);

    public Iterable<String> words();

    public Iterable<String> wordsWithPrefix(String pref);

    public boolean delete(String word);
}