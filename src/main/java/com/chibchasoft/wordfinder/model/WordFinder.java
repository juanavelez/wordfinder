package com.chibchasoft.wordfinder.model;

import java.util.List;

/**
 * Represents a way to find words using all possible combination of letters
 */
public interface WordFinder {
    /**
     * Adds a word to the list of words used to search. The word is added using the lowercase for its characters.
     * Implementations may choose to ignore/discard characters not in the 'a'-z' range when storing the word.
     * In any case, when finding words these characters will be ignored.
     * @param word The word to add.
     * @return a reference to this, so the it can be used fluently
     */
    WordFinder add(String word);

    /**
     * Returns the list of words that match all possible combinations of the letters.
     * The letters are converted to lowercase before the search is executed. The search also ignores
     * any characters not in the 'a'-'z' range.
     * @param letters The letters
     * @return the list of words ordered in descending mode by their score
     */
    List<String> getWords(String letters);
}
