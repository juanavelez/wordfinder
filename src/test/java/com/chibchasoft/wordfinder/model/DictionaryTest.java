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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.chibchasoft.wordfinder.util.Util.getLettersCount;
import static com.chibchasoft.wordfinder.util.Util.index;
import static org.junit.Assert.*;

/**
 * Test of Dictionary
 */
public class DictionaryTest {
    private Dictionary dict = new Dictionary();

    @Before
    public void setUp() {
        populateLetterPoints();
    }

    @Test
    public void testCalculateScoreNull() {
        Assert.assertEquals(0, dict.calculateScore(null));
    }

    @Test
    public void testCalculateScoreEmpty() {
        Assert.assertEquals(0, dict.calculateScore(""));
    }

    @Test
    public void testCalculateScoreOneLetter() {
        Assert.assertEquals(dict.getLetterPoints()[index('a')], dict.calculateScore("a"));
    }

    @Test
    public void testCalculateScoreTwoLetter() {
        Assert.assertEquals(dict.getLetterPoints()[index('j')]+dict.getLetterPoints()[index('z')], dict.calculateScore("jz"));
    }

    @Test
    public void testCalculateScoreInvalidLetter() {
        Assert.assertEquals(0, dict.calculateScore("^"));
    }

    @Test
    public void testEmptyDictionary() {
        Map<Byte, List<Triplet<String, Byte[], Integer>>> wordsPerLength = dict.wordsPerLength;

        assertEquals(0, wordsPerLength.size());
    }

    @Test
    public void testAddEmptyWord() {
        String letters = "";
        dict.add(letters);

        Map<Byte, List<Triplet<String, Byte[], Integer>>> wordsPerLength = dict.wordsPerLength;

        assertEquals(0, wordsPerLength.size());
    }

    @Test
    public void testAddOneWord() {
        String letters = "juana";
        Pair<Byte[], Byte> letterCountForLetters = getLettersCount(letters);
        dict.add(letters);

        Map<Byte, List<Triplet<String, Byte[], Integer>>> wordsPerLength = dict.wordsPerLength;

        assertEquals(1, wordsPerLength.size());
        List<Triplet<String, Byte[], Integer>> words = wordsPerLength.get((byte)4);
        assertNotNull(words);
        assertEquals(1, words.size());

        Triplet<String, Byte[], Integer> triplet = words.get(0);
        assertEquals(triplet.getFirst(), letters);

        Pair<Byte[], Byte> letterCountForWord = getLettersCount(triplet.getFirst());
        assertTrue(Arrays.equals(letterCountForLetters.getFirst(), letterCountForWord.getFirst()));
    }

    @Test
    public void testAddTwoWordsDifferentSize() {
        String letters1 = "juana";
        String letters2 = "hey";

        Pair<Byte[], Byte> letterCountForLetters1 = getLettersCount(letters1);
        dict.add(letters1);
        Pair<Byte[], Byte> letterCountForLetters2 = getLettersCount(letters2);
        dict.add(letters2);

        Map<Byte, List<Triplet<String, Byte[], Integer>>> wordsPerLength = dict.wordsPerLength;

        assertEquals(2, wordsPerLength.size());

        List<Triplet<String, Byte[], Integer>> wordsLength5 = wordsPerLength.get((byte)4);
        assertNotNull(wordsLength5);
        assertEquals(1, wordsLength5.size());

        Triplet<String, Byte[], Integer> triplet1 = wordsLength5.get(0);
        assertEquals(triplet1.getFirst(), letters1);

        Pair<Byte[], Byte> letterCountForWord = getLettersCount(triplet1.getFirst());
        assertTrue(Arrays.equals(letterCountForLetters1.getFirst(), letterCountForWord.getFirst()));

        List<Triplet<String, Byte[], Integer>> wordsLength3 = wordsPerLength.get((byte)3);
        assertNotNull(wordsLength3);
        assertEquals(1, wordsLength3.size());

        Triplet<String, Byte[], Integer> triplet2 = wordsLength3.get(0);
        assertEquals(triplet2.getFirst(), letters2);

        Pair<Byte[], Byte> letterCountForWord2 = getLettersCount(triplet2.getFirst());
        assertTrue(Arrays.equals(letterCountForLetters2.getFirst(), letterCountForWord2.getFirst()));
    }

    @Test
    public void testAddThreeWordsTwoWithSameSize() {
        String letters1 = "juana";
        String letters2 = "hey";
        String letters3 = "velez";

        Pair<Byte[], Byte> letterCountForLetters1 = getLettersCount(letters1);
        dict.add(letters1);
        Pair<Byte[], Byte> letterCountForLetters2 = getLettersCount(letters2);
        dict.add(letters2);
        Pair<Byte[], Byte> letterCountForLetters3 = getLettersCount(letters3);
        dict.add(letters3);

        Map<Byte, List<Triplet<String, Byte[], Integer>>> wordsPerLength = dict.wordsPerLength;

        assertEquals(2, wordsPerLength.size());

        List<Triplet<String, Byte[], Integer>> wordsLength5 = wordsPerLength.get((byte)4);
        assertNotNull(wordsLength5);
        assertEquals(2, wordsLength5.size());

        Triplet<String, Byte[], Integer> triplet1 = wordsLength5.get(0);
        assertEquals(triplet1.getFirst(), letters1);

        Pair<Byte[], Byte> letterCountForWord = getLettersCount(triplet1.getFirst());
        assertTrue(Arrays.equals(letterCountForLetters1.getFirst(), letterCountForWord.getFirst()));

        List<Triplet<String, Byte[], Integer>> wordsLength3 = wordsPerLength.get((byte)3);
        assertNotNull(wordsLength3);
        assertEquals(1, wordsLength3.size());

        Triplet<String, Byte[], Integer> triplet2 = wordsLength3.get(0);
        assertEquals(triplet2.getFirst(), letters2);

        Pair<Byte[], Byte> letterCountForWord2 = getLettersCount(triplet2.getFirst());
        assertTrue(Arrays.equals(letterCountForLetters2.getFirst(), letterCountForWord2.getFirst()));

        Triplet<String, Byte[], Integer> triplet3 = wordsLength5.get(1);
        assertEquals(triplet3.getFirst(), letters3);

        Pair<Byte[], Byte> letterCountForWord3 = getLettersCount(triplet3.getFirst());
        assertTrue(Arrays.equals(letterCountForLetters3.getFirst(), letterCountForWord3.getFirst()));
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
