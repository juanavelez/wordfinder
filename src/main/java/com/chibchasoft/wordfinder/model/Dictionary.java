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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.chibchasoft.wordfinder.util.Util.LETTERS_SIZE;
import static com.chibchasoft.wordfinder.util.Util.index;
import static com.chibchasoft.wordfinder.util.Util.getLettersCount;
import static com.chibchasoft.wordfinder.util.Util.validLetter;

/**
 * An implementation of WordFinder that stores the words in a HashMap. The key is the number of distinct letters
 * in the words and the value is a list of triplets whose first object is the word added, the second objects
 * is an array of Byte objects index by the characters in the word (0-based starting with the 'a' amd ending
 * with 25 for 'z') and the value of each element in the array is the number of instances of that character
 * in the word and the third object is the score for the word.
 *
 * Words are converted to lowercase before storing them and characters not in the range 'a'-'z' are ignored when
 * searching but the word itself is not modified.
 */
@Component("dictionary")
public class Dictionary implements WordFinder {
    private static final Logger LOG = LoggerFactory.getLogger(Dictionary.class);

    // the map to store the dictionary words as well as the statistics
    protected final Map<Byte, List<Triplet<String, Byte[], Integer>>> wordsPerLength = new HashMap<>();

    // The points assigned to letter
    protected byte[] letterPoints = new byte[LETTERS_SIZE];

    public Dictionary() {

    }

    int wordCounter = 0;
    int wordLength = 0;

    @Override
    public Dictionary add(String word) {
        LOG.debug("Adding word[" + word +"]");

        wordCounter++;
        wordLength+=word.length();

        word = word.toLowerCase();

        // Get the letter count
        Pair<Byte[], Byte> letterCount = getLettersCount(word);

        // If the distinct letter count is 0 do nothing
        if (letterCount.getSecond()==0)
            return this;

        // Get the list of pairs for this number of distinct letters in word parameter.
        List<Triplet<String, Byte[], Integer>> listLetterCount =
            wordsPerLength.computeIfAbsent(letterCount.getSecond(), k-> new ArrayList<>());

        // Add a new triplet for the word, distinct letter count and score
        listLetterCount.add(new Triplet<>(word, letterCount.getFirst(), calculateScore(word)));

        LOG.debug("Added word[" + word +"]");

        return this;
    }

    /**
     * Calculates the score for a word using {@link #getLetterPoints()}
     * @param word the word
     * @return the score
     */
    protected int calculateScore(String word) {
        if (word == null || word.isEmpty())
            return 0;

        byte score = 0;
        for(char c: word.toCharArray()) {
            if (validLetter(c))
                score += letterPoints[index(c)];
        }

        return score;
    }

    @Override
    public List<String> getWords(String letters) {
        LOG.debug("Finding all possible words using [" + letters +"]");

        // if no letter or empty return an empty list
        if (letters == null || letters.isEmpty())
            return Collections.emptyList();

        // A list of pairs whose first object is the word found and the second its total score
        List<Pair<String, Integer>> words = new ArrayList<>();

        // Get the distinct letter count for the word
        Pair<Byte[], Byte> letterCount = getLettersCount(letters);

        // Iterate up to the number of distinct letters in "letters"
        // Start with 1 because we don't care about 0 words
        for (byte i=1; i<=letterCount.getSecond(); i++) {
            // Find out if there are words whose number of distinct letters match the number of letters for this iteration
            List<Triplet<String, Byte[], Integer>> listLetterCount = wordsPerLength.get(i);

            // If no words, continue with the next iteration
            if (listLetterCount == null)
                continue;

            listLetterCount.forEach(triple -> {
                // For each word iterate over its distinct letters and if the letter exist in the "letters"
                // and the number of times of that letter is less or equal to the number of times of that letter
                // which is being compare, then a match is found
                boolean ok = true;

                Byte[] lc = triple.getSecond();

                for( int j=0; j<lc.length; j++) {
                    if (lc[j] != null && (letterCount.getFirst()[j] == null || lc[j] > letterCount.getFirst()[j])) {
                        ok = false;
                        break;
                    }
                }

                if (ok)
                    words.add(new Pair<>(triple.getFirst(), triple.getThird()));
            });
        }

        // We need to sort (descending) the words based on their score
        words.sort((r1, r2) -> Integer.compare(r2.getSecond(), r1.getSecond()));

        LOG.debug("Found [" + words.size() + "] words for [" +letters + "]");

        return words.stream().map(Pair::getFirst).collect(Collectors.toList());
    }

    /**
     * Get the points assigned to letters which will be used to determine the score of a word
     * @return an array of bytes with the points for each letter, indexed by the character position in the range 'a'-'z'
     */
    public byte[] getLetterPoints() {
        return letterPoints;
    }

    /**
     * Set the points assigned to letters which will be used to determine the score of a word
     * @param letterPoints an array of bytes with the points for each letter,
     * indexed by the character position in the range 'a'-'z'
     */
    @Resource(name = "letterPoints")
    public void setLetterPoints(byte[] letterPoints) {
        this.letterPoints = letterPoints;
    }
}
