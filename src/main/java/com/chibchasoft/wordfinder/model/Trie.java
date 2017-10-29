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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.chibchasoft.wordfinder.util.Util.*;

/**
 * A <a href="https://en.wikipedia.org/wiki/Trie">Trie</a> used to store dictionary words as well
 * as to retrieve them. This is a very specific specialization as it holds only the 26 lowercase
 * characters used in the english language.
 * In addition it keeps track of the "totalPoints" for each node from the root up to this node.
 */
public class Trie implements WordFinder {
    private static final Logger LOG = LoggerFactory.getLogger(Trie.class);

    protected Node root = new Node();

    // The points assigned to letter
    protected byte[] letterPoints = new byte[LETTERS_SIZE];

    public Trie() {

    }

    /**
     * Add a word into this Trie. The word is added using the lowercase for its characters.
     * Invalid characters (as defined by {@link Util#validLetter} are ignored
     *
     * @param word The word to add.
     * @return itself so it can fluent
     */
    @Override
    public Trie add(String word) {
        LOG.debug("Adding word[" + word +"]");

        root.add(word.toLowerCase(), letterPoints,0);

        LOG.debug("Added word[" + word +"]");

        return this;
    }

    /**
     * Using the provided letters find all possible words
     * @param letters the string with the letters to use to find words
     * @return the list of words
     */
    @Override
    public List<String> getWords(String letters) {
        LOG.debug("Finding all possible words using [" + letters +"]");

        if (letters == null || letters.isEmpty())
            return Collections.emptyList();

        LetterIndices letterIndices = new LetterIndices();
        letterIndices.calculateIndices(letters.toLowerCase());

        List<Pair<String, Integer>> words = new ArrayList<>();

        Node node = root;

        // for each letter, find out if there is a child node and if so, delegate to the
        // node itself to find all words
        for (int i = 0; i < LETTERS_SIZE; i++) {
            // We need to search for words if the element at the index is set
            if (letterIndices.isPresent(i) && node.children[i] != null) {
                // The letter is present so it becomes part of the word
                StringBuilder wordBuilder = new StringBuilder();
                wordBuilder.append((char) (i + FIRST_LETTER));

                // Because we found an entry for this letter, we need to decrement it
                letterIndices.decrementForIndex(i);

                node.children[i].findWords(letterIndices, wordBuilder, words);

                letterIndices.incrementForIndex(i);
            }
        }

        LOG.debug("Found [" + words.size() + "] words for [" +letters + "]");

        // We need to sort (descending) the words based on their total points
        words.sort((r1, r2) -> Integer.compare(r2.getSecond(), r1.getSecond()));

        return words.stream().map(r -> r.getFirst()).collect(Collectors.toList());
    }

    /**
     * Resets this dictionary
     */
    public void reset() {
        root = new Node();
        letterPoints = new byte[LETTERS_SIZE];;
    }

    /**
     * The node for the trie
     */
    protected static class Node {
        public final static AtomicInteger nodeCounter = new AtomicInteger(0);

        // Indicates whether the chain from the root to this node makes a word
        protected boolean isWord = false;

        // The total points at this node for the chain from the root to this node
        protected int totalPoints = 0;

        // The different paths from which the word at this point can take.
        // Each element represents a letter beginning with 'a' at index 0 and finishing with 'z' at index 25
        protected Node[] children = new Node[LETTERS_SIZE];

        public Node() {
            nodeCounter.incrementAndGet();
        }

        /**
         * Add a word to this node
         *
         * @param word        the word to add
         * @param letterPoints the points for each letter
         * @param totalPoints the total points so far
         */
        public void add(String word, byte[] letterPoints, int totalPoints) {
            if (word == null || word.isEmpty())
                return;

            Node node = this;

            int wordLength = word.length();

            // Each character of the word becomes a new node whose index corresponds to the character index (0-based)
            // and keeps track of how many points is worth the word at that point
            for (int i = 0; i < wordLength; i++) {
                char c = word.charAt(i);

                // Ignore any character that it is not within the range of letters
                if (!validLetter(c))
                    continue;

                int index = index(c);

                if (node.children[index] == null) {
                    node.children[index] = new Node();
                }

                totalPoints += letterPoints[index(c)];
                node.children[index].totalPoints = totalPoints;

                node = node.children[index];
            }

            node.isWord = true;
        }

        /**
         * Find words that are matched from this point of the node forward according to the supplied letters.
         * Words found are added to the results parameters
         *
         * @param letterIndices The letters to look for
         * @param wordBuilder   the word built so far
         * @param words         the list to which to add the found words
         */
        public void findWords(LetterIndices letterIndices, StringBuilder wordBuilder, List<Pair<String, Integer>> words) {
            // if at this node is a word, then add it to the results.
            if (isWord) {
                words.add(new Pair<>(wordBuilder.toString(), totalPoints));
            }

            // if we got here but there are no more letters to search return
            if (letterIndices.isEmpty())
                return;

            for (int i = 0; i < LETTERS_SIZE; i++) {
                if (letterIndices.isPresent(i) && children[i] != null) {
                    // add current character
                    StringBuilder newWordBuilder = new StringBuilder(wordBuilder).append((char) (i + FIRST_LETTER));

                    // Because we are in this index, we need one less instance of this letter when
                    // searching the children
                    letterIndices.decrementForIndex(i);

                    // Recursively search reaming letters in the children
                    children[i].findWords(letterIndices, newWordBuilder, words);

                    // Increment the instance of the letter
                    letterIndices.incrementForIndex(i);
                }
            }
        }
    }


    public byte[] getLetterPoints() {
        return letterPoints;
    }

    public void setLetterPoints(byte[] letterPoints) {
        this.letterPoints = letterPoints;
    }

    /**
     * Keeps track of the quantities for each of the letters
     */
    protected static class LetterIndices {
        protected byte[] indices = new byte[LETTERS_SIZE];
        protected int total = 0;

        /**
         * Calculates the quantity of each letter in the string and stores them in the internal array
         * @param letters the string with letters
         */
        public void calculateIndices(String letters) {
            for (char c : letters.toCharArray()) {
                // Ignore any character that it is not within the range of letters
                if (!validLetter(c))
                    continue;

                indices[index(c)]++;
                total++;
            }
        }

        /**
         * Increases the quantity of the letter at the index
         * @param index the index
         */
        public void incrementForIndex(int index) {
            indices[index]++;
            total++;
        }

        /**
         * Decreases the quantity of the letter at the index
         */
        public void decrementForIndex(int index) {
            indices[index]--;
            total--;
        }

        /**
         * Indicates whether the quantity of the letter at index is greater than 0
         * @param index the index
         * @return True if quantity of the letter at index is greater than 0
         */
        public boolean isPresent(int index) {
            return indices[index] > 0;
        }

        /**
         * Indicates whether all elements have quantity of 0
         * @return True if all elements have quantity of 0
         */
        public boolean isEmpty() {
            return total == 0;
        }
    }
}
