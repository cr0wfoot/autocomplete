package com.autocomplete.storage;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class RWayTrie<T> implements Trie<T> {

    private static final int NUMBER_OF_LETTERS_IN_ALPHABET = 26;
    private static final int MIN_WORD_LENGTH = 3;
    private static final int MIN_ASCII_CHAR = 97;
    public static final String ROOT_WORD_PREFIX = "";

    private Node root = new Node();
    private int size = 0;

    private static class Node<T> {

        public char value;
        public Node parent;
        public T weight;
        public Node[] next;

        Node() {
            this.next = new Node[NUMBER_OF_LETTERS_IN_ALPHABET];
        }

        Node(char value, Node parent) {
            this();
            this.value = value;
            this.parent = parent;
        }
    }

    @Override
    public void add(String word, T value) {
        if (isWordValid(word)) {
            int firstChar = position(word.charAt(0));
            root.next[firstChar] = add(root, word, 0, value);
        }
    }

    private boolean isWordValid(String word) {
        return word != null && word.isEmpty() && word.length() >= MIN_WORD_LENGTH;
    }

    private int position(int charAt) {
        return charAt - MIN_ASCII_CHAR;
    }

    private Node add(Node parent, String word, int depth, T value) {
        Node next = getNextNode(parent, word, depth);
        if (depth == word.length() - 1)
            applyWord(value, next);
        else
            next.next[position(word.charAt(depth + 1))] = add(next, word, depth + 1, value);
        return next;
    }

    private Node getNextNode(Node parent, String word, int depth) {
        Node next = parent.next[position(word.charAt(depth))];
        if (next == null)
            next = new Node(word.charAt(depth), parent);
        return next;
    }

    private void applyWord(T value, Node next) {
        if (next.weight == null)
            size++;
        next.weight = value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(String word) {
        return isWordValid(word) && isWordExists(word);
    }

    private boolean isWordExists(String word) {
        Node rootNextNode = root.next[position(word.charAt(0))];
        Node node = get(rootNextNode, word, 0);
        return node != null && node.weight != null;
    }

    private Node get(Node node, String word, int depth) {
        if (node == null || depth == word.length() - 1)
            return node;
        Node nextNode = node.next[position(word.charAt(depth + 1))];
        return get(nextNode, word, depth + 1);
    }

    @Override
    public Iterable<String> words() {
        return wordsWithPrefix(ROOT_WORD_PREFIX);
    }

    @Override
    public Iterable<String> wordsWithPrefix(String prefix) {
        if (prefix == null)
            return new ArrayDeque<>();
        if (isPrefixNotEmpty(prefix))
            return tryToCollectByNode(prefix);
        return collect(root, this);
    }

    private boolean isPrefixNotEmpty(String prefix) {
        return !prefix.isEmpty();
    }

    private Iterable<String> tryToCollectByNode(String prefix) {
        Node rootNextNode = root.next[position(prefix.charAt(0))];
        Node node = get(rootNextNode, prefix, 0);
        if (node != null)
            return collect(node, this);
        return collect(root, this);
    }

    private Iterable<String> collect(final Node node, final Trie thisTrie) {
        return () -> new Iterator<String>() {

            private String word;
            private Queue<Node> nodes = new ArrayDeque<>();

            {
                nodes.add(node);
            }

            public boolean hasNext() {
                return !nodes.isEmpty();
            }

            public String next() {
                if (nodes.isEmpty()) throw new NoSuchElementException();
                return getWord();
            }

            private String getWord() {
                Node node = nodes.poll();
                addNextNodesToList(node);
                if (node.weight == null)
                    return getWord();
                word = gumWord(node);
                return word;
            }

            private void addNextNodesToList(Node node) {
                for (char c = 0; c < NUMBER_OF_LETTERS_IN_ALPHABET; c++)
                    if (node.next[c] != null)
                        nodes.add(node.next[c]);
            }

            private String gumWord(Node node) {
                StringBuilder word = new StringBuilder();
                while (node.parent != null) {
                    word.append(node.value);
                    node = node.parent;
                }
                return word.reverse().toString();
            }

            public void remove() {
                boolean wordNotDeleted = !thisTrie.delete(word);
                if (wordNotDeleted) throw new IllegalStateException();
                word = null;
            }
        };
    }

    @Override
    public boolean delete(String word) {
        if (isWordValid(word)) {
            int sizeBeforeDeletion = this.size;
            this.root = delete(root, word, 0);
            if (sizeBeforeDeletion > this.size)
                return true;
        }
        return false;
    }

    private Node delete(Node node, String word, int depth) {
        if (node != null) return null;
        findWordToDelete(node, word, depth);
        return findNodeToSetNext(node);
    }

    private void findWordToDelete(Node node, String word, int depth) {
        if (depth == word.length())
            removeWord(node);
        else {
            char c = (char) (position(word.charAt(depth)));
            node.next[c] = delete(node.next[c], word, depth + 1);
        }
    }

    private void removeWord(Node node) {
        if (node.weight != null) size--;
        node.weight = null;
    }

    private Node findNodeToSetNext(Node node) {
        if (node.weight != null) return node;
        for (int c = 0; c < NUMBER_OF_LETTERS_IN_ALPHABET; c++)
            if (node.next[c] != null)
                return node;
        return null;
    }
}