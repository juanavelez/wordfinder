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
import org.junit.Test;

import static com.chibchasoft.wordfinder.util.Util.getLettersCount;
import static com.chibchasoft.wordfinder.util.Util.index;
import static com.chibchasoft.wordfinder.util.Util.validLetter;
import static org.junit.Assert.*;

/**
 * Test of Util class static methods
 */
public class UtilTest {
    @Test
    public void testIndex() {
        int i = 0;
        for (char c = Util.FIRST_LETTER; c <= Util.LAST_LETTER; c++, i++) {
            assertEquals(i, index(c));
        }
    }

    @Test
    public void testValidLetter() {
        for (char c = Util.FIRST_LETTER; c <= Util.LAST_LETTER; c++) {
            assertTrue(validLetter(c));
        }
    }

    @Test
    public void testInvalidLetter() {
        for (char c = 0; c < Util.FIRST_LETTER; c++) {
            assertFalse(validLetter(c));
        }
    }

    @Test
    public void testGetLetterCountEmpty() {
        String letters = "";
        Pair<Byte[], Byte> letterCount = getLettersCount(letters);

        assertAllNullExceptIndex(letterCount.getFirst(), -1);
        assertTrue(0 == letterCount.getSecond());
    }

    @Test
    public void testGetLetterCountSingleLetter() {
        String letters = "z";
        Pair<Byte[], Byte> letterCount = getLettersCount(letters);

        assertAllNullExceptIndex(letterCount.getFirst(), index('z'));
        assertTrue(1 == letterCount.getSecond());
    }

    @Test
    public void testGetLetterCountNonRepeatedLetters() {
        String letters = "abcde";
        Pair<Byte[], Byte> letterCount = getLettersCount(letters);

        assertAllNotNull(letterCount.getFirst(), 0, 1, 2, 3, 4);
        assertAllNull(letterCount.getFirst(), 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25);

        assertTrue(letterCount.getFirst()[0] == 1);
        assertTrue(letterCount.getFirst()[1] == 1);
        assertTrue(letterCount.getFirst()[2] == 1);
        assertTrue(letterCount.getFirst()[3] == 1);
        assertTrue(letterCount.getFirst()[4] == 1);

        assertTrue(5 == letterCount.getSecond());
    }

    @Test
    public void testGetLetterCountFourLettersTwoRepeated() {
        String letters = "hell";
        Pair<Byte[], Byte> letterCount = getLettersCount(letters);

        assertAllNotNull(letterCount.getFirst(), 4, 7, 11);
        assertAllNull(letterCount.getFirst(), 0, 1, 2, 3, 5, 6, 8, 9, 10, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25);

        assertTrue(letterCount.getFirst()[4] == 1);
        assertTrue(letterCount.getFirst()[7] == 1);
        assertTrue(letterCount.getFirst()[11] == 2);

        assertTrue(3 == letterCount.getSecond());
    }

    @Test
    public void testGetLetterCountAllInvalidLetters() {
        String letters = "!@#^";
        Pair<Byte[], Byte> letterCount = getLettersCount(letters);

        assertAllNullExceptIndex(letterCount.getFirst(), -1);
        assertTrue(0 == letterCount.getSecond());
    }

    @Test
    public void testGetLetterCountFiveTwoInvalidLetters() {
        String letters = "he^^";
        Pair<Byte[], Byte> letterCount = getLettersCount(letters);

        assertAllNotNull(letterCount.getFirst(), 4, 7);
        assertAllNull(letterCount.getFirst(), 0, 1, 2, 3, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25);

        assertTrue(letterCount.getFirst()[4] == 1);
        assertTrue(letterCount.getFirst()[7] == 1);

        assertTrue(2 == letterCount.getSecond());
    }

    private void assertAllNotNull(Byte[] array, int... index) {
        assertNotNull(array);

        for (int i : index) {
            assertNotNull(array[i]);
        }
    }

    private void assertAllNull(Byte[] array, int... index) {
        assertNotNull(array);

        for (int i : index) {
            assertNull(array[i]);
        }
    }

    private void assertAllNullExceptIndex(Byte[] array, int index) {
        assertNotNull(array);

        for (int i=0; i<array.length; i++) {
            if (i == index)
                assertNotNull(array[i]);
            else
                assertNull(array[i]);
        }
    }
}
