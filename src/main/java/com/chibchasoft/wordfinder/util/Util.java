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
package com.chibchasoft.wordfinder.util;

import com.chibchasoft.wordfinder.model.Pair;

/**
 * A collection of static utility methods and constants
 */
public final class Util {
    /**
     * The number of letters the application will use
     */
    public static final int LETTERS_SIZE = 26;

    /**
     * The first 'char' of the letters
     */
    public static final char FIRST_LETTER = 'a';

    /**
     * The last 'char' of the letters
     */
    public static final char LAST_LETTER = 'z';

    /**
     * Indicates whether the character passed is a valid letter
     * @param c the character to test
     * @return true if it's a valid letter
     */
    public static boolean validLetter(char c) {
        return c >= Util.FIRST_LETTER && c <= Util.LAST_LETTER;
    }

    /**
     * Returns the index (zero-based) for the passed letter in the range of valid letters
     * @param c the character
     * @return the index
     */
    public static byte index(char c) {
        return (byte) (c - Util.FIRST_LETTER);
    }

    /**
     * Returns a pair whose first object is an array of Bytes indexed by the character position in the range 'a'-'z'
     * which is populated if the word has a character for that index and for such index, it counts how many times that
     * character is present in the word. The second object is the number of actual Byte objects is the array.
     * @param word the word
     * @return the pair
     */
    public static Pair<Byte[], Byte> getLettersCount(String word) {
        Byte[] letterCount = new Byte[LETTERS_SIZE];

        byte count = 0;
        for (char c : word.toLowerCase().toCharArray()) {
            if (validLetter(c)) {

                int i = index(c);

                if (letterCount[i] == null) {
                    count++;
                    letterCount[i] = 0;
                }

                letterCount[i]++;
            }
        }

        return new Pair<>(letterCount, count);
    }
}
