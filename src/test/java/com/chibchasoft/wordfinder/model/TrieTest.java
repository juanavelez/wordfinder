/*
 * Copyright (c) 2017 chibchasoft.com
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution.
 *
 *      The Apache License v2.0 is available at
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Author <a href="mailto:jvelez@chibchasoft.com">Juan Velez</a>
 */
package com.chibchasoft.wordfinder.model;

import com.chibchasoft.wordfinder.util.Util;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.chibchasoft.wordfinder.util.Util.index;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test of Trie
 */
public class TrieTest {
    private Trie dict = new Trie();

    @Before
    public void setUp() {
        populateLetterPoints();
    }

    @Test
    public void testEmptyDictionary() {
        assertNotNull(dict.root);
        assertFalse(dict.root.isWord);
        assertEquals(0, dict.root.totalPoints);
        assertNotNull(dict.root.children);
        for (int i=0; i<dict.root.children.length; i++) {
            assertNull(dict.root.children[i]);
        }
    }

    @Test
    public void testAddEmptyWord() {
        String letters = "";
        dict.add(letters);

        assertNotNull(dict.root);
        assertFalse(dict.root.isWord);
        assertEquals(0, dict.root.totalPoints);
        assertNotNull(dict.root.children);
        for (int i=0; i<dict.root.children.length; i++) {
            assertNull(dict.root.children[i]);
        }
    }

    @Test
    public void testAddOneWord() {
        String letters = "juana";
        dict.add(letters);

        assertNotNull(dict.root);
        assertFalse(dict.root.isWord);
        assertEquals(0, dict.root.totalPoints);
        assertNotNull(dict.root.children);

        int totalPoints = 0;
        int letterCounter = 1;

        Trie.Node node = dict.root;
        for (char c : letters.toCharArray()) {
            byte index = index(c);
            for (int i=0; i<node.children.length; i++) {
                if (i==index) {
                    totalPoints += dict.letterPoints[i];
                    assertNotNull(node.children[i]);
                    assertEquals(node.children[i].totalPoints, totalPoints);
                    if (letterCounter == letters.length())
                        assertTrue(node.children[i].isWord);
                    else
                        assertFalse(node.children[i].isWord);
                }
                else {
                    assertNull(node.children[i]);
                }
            }

            letterCounter++;
            node = node.children[index];
        }
    }

    @Test
    public void testAddTwoWordsNoIntersectLetters() {
        String letters1 = "juana";
        String letters2 = "hey";
        dict.add(letters1);
        dict.add(letters2);

        assertNotNull(dict.root);
        assertFalse(dict.root.isWord);
        assertEquals(0, dict.root.totalPoints);
        assertNotNull(dict.root.children);

        int totalPoints = 0;
        int letterCounter = 1;

        Trie.Node node = dict.root;
        for (char c : letters1.toCharArray()) {
            byte index = index(c);
            for (int i=0; i<node.children.length; i++) {
                if (i==index) {
                    totalPoints += dict.letterPoints[i];
                    assertNotNull(node.children[i]);
                    assertEquals(node.children[i].totalPoints, totalPoints);
                    if (letterCounter == letters1.length())
                        assertTrue(node.children[i].isWord);
                    else
                        assertFalse(node.children[i].isWord);
                }
                else if (letterCounter == 1 && i == index('h'))
                    assertNotNull(node.children[i]);
                else {
                    assertNull(node.children[i]);
                }
            }

            letterCounter++;
            node = node.children[index];
        }

        totalPoints = 0;
        letterCounter = 1;

        node = dict.root;
        for (char c : letters2.toCharArray()) {
            byte index = index(c);
            for (int i=0; i<node.children.length; i++) {
                if (i==index) {
                    totalPoints += dict.letterPoints[i];
                    assertNotNull(node.children[i]);
                    assertEquals(node.children[i].totalPoints, totalPoints);
                    if (letterCounter == letters2.length())
                        assertTrue(node.children[i].isWord);
                    else
                        assertFalse(node.children[i].isWord);
                }
                else if (letterCounter == 1 && i == index('j'))
                    assertNotNull(node.children[i]);
                else {
                    assertNull(node.children[i]);
                }
            }

            letterCounter++;
            node = node.children[index];
        }
    }

    @Test
    public void testAddTwoWordsWithIntersectLetters() {
        String letters1 = "hell";
        String letters2 = "hello";
        dict.add(letters1);
        dict.add(letters2);

        assertNotNull(dict.root);
        assertFalse(dict.root.isWord);
        assertEquals(0, dict.root.totalPoints);
        assertNotNull(dict.root.children);

        int totalPoints = 0;
        int letterCounter = 1;

        Trie.Node node = dict.root;
        for (char c : letters2.toCharArray()) {
            byte index = index(c);
            for (int i=0; i<node.children.length; i++) {
                if (i==index) {
                    totalPoints += dict.letterPoints[i];
                    assertNotNull(node.children[i]);
                    assertEquals(node.children[i].totalPoints, totalPoints);
                    if (letterCounter == letters1.length() || letterCounter == letters2.length())
                        assertTrue(node.children[i].isWord);
                    else
                        assertFalse(node.children[i].isWord);
                }
                else {
                    assertNull(node.children[i]);
                }
            }

            letterCounter++;
            node = node.children[index];
        }
    }


    @Test
    public void testFindWordsThatExist() {
        List<String> words = Arrays.asList("a", "ah",  "at", "ha", "hat", "juana");
        words.forEach(dict::add);

        List<String> results = dict.getWords("tha");

        assertNotNull(results);
        assertEquals(results.size(), 5);

        assertTrue(words.containsAll(results));
    }

    @Test
    public void testFindWordsThatExistRepeatedLetter() {
        List<String> words = Arrays.asList("he", "hell",  "hello", "how", "yell", "yellow");
        words.forEach(dict::add);

        List<String> results = dict.getWords("hello");

        assertNotNull(results);
        assertEquals(results.size(), 3);

        assertTrue(Arrays.asList("he", "hell",  "hello").containsAll(results));
    }

    @Test
    public void testFindWordsNoMatchingNumberOfLetters() {
        List<String> words = Arrays.asList("hee", "lad");
        words.forEach(dict::add);

        List<String> results = dict.getWords("hel");

        assertNotNull(results);
        assertEquals(results.size(), 0);

        assertTrue(Arrays.asList("he").containsAll(results));
    }

    @Test
    public void testFindWordsThatDontExist() {
        List<String> words = Arrays.asList("he", "hell",  "hello", "how", "yell", "yellow");
        words.forEach(dict::add);

        List<String> results = dict.getWords("xxx");

        assertNotNull(results);
        assertEquals(results.size(), 0);
    }

    @Test
    public void testFindWordsForEmptyLetters() {
        List<String> words = Arrays.asList("he", "hell",  "hello", "how", "yell", "yellow");
        words.forEach(dict::add);

        List<String> results = dict.getWords("");

        assertNotNull(results);
        assertEquals(results.size(), 0);
    }

    @Test
    public void testFindWordsForNullLetters() {
        List<String> words = Arrays.asList("he", "hell",  "hello", "how", "yell", "yellow");
        words.forEach(dict::add);

        List<String> results = dict.getWords(null);

        assertNotNull(results);
        assertEquals(results.size(), 0);
    }

    @After
    public void tearDown() {
        dict.reset();
    }

    private void populateLetterPoints() {
        byte[] letterPoints = new byte[Util.LETTERS_SIZE];
        letterPoints[index('a')]=1;
        letterPoints[index('e')]=1;
        letterPoints[index('i')]=1;
        letterPoints[index('l')]=1;
        letterPoints[index('n')]=1;
        letterPoints[index('o')]=1;
        letterPoints[index('r')]=1;
        letterPoints[index('s')]=1;
        letterPoints[index('t')]=1;
        letterPoints[index('u')]=1;

        letterPoints[index('d')]=2;
        letterPoints[index('g')]=2;

        letterPoints[index('b')]=3;
        letterPoints[index('c')]=3;
        letterPoints[index('m')]=3;
        letterPoints[index('p')]=3;

        letterPoints[index('f')]=4;
        letterPoints[index('h')]=4;
        letterPoints[index('v')]=4;
        letterPoints[index('w')]=4;
        letterPoints[index('y')]=4;

        letterPoints[index('k')]=5;

        letterPoints[index('j')]=8;
        letterPoints[index('x')]=8;

        letterPoints[index('q')]=10;
        letterPoints[index('z')]=10;

        dict.setLetterPoints(letterPoints);
    }
}
