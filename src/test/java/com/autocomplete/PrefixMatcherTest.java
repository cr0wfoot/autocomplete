package com.autocomplete;

import com.autocomplete.storage.RWayTrie;
import com.autocomplete.storage.Trie;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PrefixMatcherTest {

    private static final String FIRST_WORD = "key";
    private static final String SECOND_WORD = "keys";
    private static final String INVALID_WORD = "w";
    private static final String INVALID_PREFIX = "k";
    private static final String EMPTY_STRING = "";
    public static final String VALID_PREFIX = "ke";
    public static final String WORDS_DELIMITER = " ";

    @Mock
    private Trie trie;

    @InjectMocks
    private PrefixMatcher instance;

    private Iterator<String> prefixMatcherIterator;

    @Before
    public void setUp() {
        trie = new RWayTrie();
        instance = new PrefixMatcher(trie);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnFalseIfDeleteNull() {
        when(trie.delete(null)).thenReturn(false);

        assertFalse(instance.delete(null));
    }

    @Test
    public void shouldReturnFalseIfDeleteWordWhichNotExist() {
        when(trie.delete(FIRST_WORD)).thenReturn(false);

        assertFalse(instance.delete(FIRST_WORD));
    }

    @Test
    public void shouldReturnFalseIfDeleteInvalidWord() {
        when(trie.delete(INVALID_WORD)).thenReturn(false);

        assertFalse(instance.delete(INVALID_WORD));
    }

    @Test
    public void shouldReturnTrueWhenDeleteWord() {
        when(trie.delete(FIRST_WORD)).thenReturn(true);

        assertTrue(instance.delete(FIRST_WORD));
    }

    @Test
    public void shouldReturnFalseWhenContainsNull() {
        when(trie.contains(null)).thenReturn(false);

        assertFalse(instance.contains(null));
    }

    @Test
    public void shouldReturnFalseWhenContainsEmptyWord() {
        when(trie.contains(EMPTY_STRING)).thenReturn(false);

        assertFalse(instance.contains(EMPTY_STRING));
    }

    @Test
    public void shouldReturnFalseWhenContainsWordWithUnacceptableLength() {
        when(trie.contains(INVALID_WORD)).thenReturn(false);

        assertFalse(instance.contains(INVALID_WORD));
    }

    @Test
    public void shouldReturnFalseIfWordNotExists() {
        when(trie.contains(FIRST_WORD)).thenReturn(false);

        assertFalse(instance.contains(FIRST_WORD));
    }

    @Test
    public void shouldReturnTrueIfWordExists() {
        when(trie.contains(FIRST_WORD)).thenReturn(true);

        assertTrue(instance.contains(FIRST_WORD));
    }

    @Test
    public void shouldReturnZeroSizeIfEmpty() {
        when(trie.size()).thenReturn(0);

        assertEquals(0, instance.size());
    }

    @Test
    public void shouldAddTwoWordsWhenAddStringWithThreeWordsWithOneUnacceptableWord() {
        String threeWords = FIRST_WORD + WORDS_DELIMITER + SECOND_WORD + WORDS_DELIMITER + INVALID_WORD;
        int addedWordsAmount = 2;

        assertEquals(addedWordsAmount, instance.add(threeWords));
        verify(trie).add(FIRST_WORD, FIRST_WORD.length());
        verify(trie).add(SECOND_WORD, SECOND_WORD.length());
        verify(trie, never()).add(INVALID_WORD, INVALID_WORD.length());
    }

    @Test
    public void shouldAddTwoWordsWhenAddStringArrayWithBadData() {
        String[] badData = {FIRST_WORD + WORDS_DELIMITER + SECOND_WORD, null, EMPTY_STRING, WORDS_DELIMITER};
        int addedWordsAmount = 2;

        assertEquals(addedWordsAmount, instance.add(badData));
        verify(trie).add(FIRST_WORD, FIRST_WORD.length());
        verify(trie).add(SECOND_WORD, SECOND_WORD.length());
    }

    @Test
    public void shouldReturnWordsWithAcceptablePrefix() {
        when(trie.wordsWithPrefix(VALID_PREFIX)).thenReturn(new ArrayDeque(Arrays.asList(FIRST_WORD, SECOND_WORD)));
        prefixMatcherIterator = instance.wordsWithPrefix(VALID_PREFIX).iterator();

        assertTrue(prefixMatcherIterator.hasNext());
        assertEquals(FIRST_WORD, prefixMatcherIterator.next());
        assertTrue(prefixMatcherIterator.hasNext());
        assertEquals(SECOND_WORD, prefixMatcherIterator.next());
        assertFalse(prefixMatcherIterator.hasNext());
    }

    @Test
    public void shouldReturnFalseIfTryToGetWordsByEmptyPrefix() {
        when(trie.wordsWithPrefix(EMPTY_STRING)).thenReturn(new ArrayDeque(Arrays.asList(FIRST_WORD)));
        prefixMatcherIterator = instance.wordsWithPrefix(EMPTY_STRING).iterator();

        assertFalse(prefixMatcherIterator.hasNext());
    }

    @Test
    public void shouldReturnFalseIfTryToGetWordsByNullPrefix() {
        when(trie.wordsWithPrefix(null)).thenReturn(new ArrayDeque());

        prefixMatcherIterator = instance.wordsWithPrefix(null).iterator();

        assertFalse(prefixMatcherIterator.hasNext());
    }

    @Test
    public void shouldReturnFalseIfTryToGetWordsByUnacceptablePrefix() {
        when(trie.wordsWithPrefix(INVALID_PREFIX)).thenReturn(new ArrayDeque(Arrays.asList(INVALID_PREFIX)));
        prefixMatcherIterator = instance.wordsWithPrefix(INVALID_PREFIX).iterator();

        assertFalse(prefixMatcherIterator.hasNext());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenTryToGetWordsWithDepthValueBelowZero() {
        int depth = -1;

        instance.wordsWithPrefix(VALID_PREFIX, depth).iterator();
    }

    @Test
    public void shouldReturnOneWordWhenTryToGetWordsWithDepthValueZero() {
        String word = "ke";
        int depth = 0;

        when(trie.wordsWithPrefix(VALID_PREFIX)).thenReturn(new ArrayDeque(Arrays.asList(word, FIRST_WORD)));
        prefixMatcherIterator = instance.wordsWithPrefix(VALID_PREFIX, depth).iterator();

        assertTrue(prefixMatcherIterator.hasNext());
        assertEquals(word, prefixMatcherIterator.next());
        assertFalse(prefixMatcherIterator.hasNext());
    }

    @Test
    public void shouldReturnOneWordWhenTryToGetWordsWithDepthValueOne() {
        String prefix = "key";
        int depth = 1;

        when(trie.wordsWithPrefix(prefix)).thenReturn(new ArrayDeque(Arrays.asList(FIRST_WORD, SECOND_WORD)));
        prefixMatcherIterator = instance.wordsWithPrefix(prefix, depth).iterator();

        assertTrue(prefixMatcherIterator.hasNext());
        assertEquals(FIRST_WORD, prefixMatcherIterator.next());
        assertFalse(prefixMatcherIterator.hasNext());
    }

    @Test
    public void shouldNotReturnWordsIfPrefixNull() {
        when(trie.wordsWithPrefix(null)).thenReturn(new ArrayDeque());
        prefixMatcherIterator = instance.wordsWithPrefix(null, 0).iterator();

        assertFalse(prefixMatcherIterator.hasNext());
    }

    @Test
    public void shouldNotReturnWordsIfPrefixUnacceptable() {
        when(trie.wordsWithPrefix(INVALID_PREFIX)).thenReturn(new ArrayDeque(Arrays.asList(INVALID_PREFIX)));
        prefixMatcherIterator = instance.wordsWithPrefix(INVALID_PREFIX, 0).iterator();

        assertFalse(prefixMatcherIterator.hasNext());
    }
}