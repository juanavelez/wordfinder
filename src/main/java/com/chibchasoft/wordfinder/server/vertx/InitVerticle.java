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
package com.chibchasoft.wordfinder.server.vertx;

import com.chibchasoft.wordfinder.model.WordFinder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Vertx verticle that loads the word into a WordFinder.
 */
@Component("initVerticle")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InitVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(InitVerticle.class);

    private Resource wordsLocation;

    private WordFinder wordFinder;

    public InitVerticle() {
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        if (wordFinder == null) {
            String msg = "WordFinder not set";
            LOG.warn(msg);
            startFuture.fail(msg);
            return;
        }

        Future<Void> loadWordsFuture = Future.future();

        loadWordsFuture.setHandler(startFuture.completer());

        context.runOnContext(v2 -> loadWords(loadWordsFuture));
    }

    /**
     * Load the words into the Trie
     * @param future the future to fail or complete
     */
    private void loadWords(Future<Void> future) {
        if (wordsLocation == null || ! wordsLocation.exists() ) {
            future.fail("wordsLocation[" + wordsLocation + "] does not exist");
            return;
        }

        try (InputStream wis = wordsLocation.getInputStream();
             InputStreamReader isr = new InputStreamReader(wis);
             BufferedReader br = new BufferedReader(isr)) {

            String word;
            int wc = 0;
            while ((word = br.readLine()) != null) {
                LOG.debug("adding " + word + "to WordFinder");
                wordFinder.add(word);
                wc++;
            }

            LOG.info("Loaded " + wc + " words into WordFinder");

            future.complete();
        } catch (IOException e) {
            String msg = "an error occurred while trying to read the words";
            LOG.warn(msg, e);
            future.fail(e);
        }
    }

    /**
     * Get the location where to find the words to load
     * @return the location where to find the words to load
     */
    public Resource getWordsLocation() {
        return wordsLocation;
    }

    /**
     * Sets the location where to find the words to load
     * @param wordsLocation the location where to find the words to load
     */
    @Value("${words.location:classpath:english-words.txt}")
    public void setWordsLocation(Resource wordsLocation) {
        this.wordsLocation = wordsLocation;
    }

    /**
     * Gets the {@link WordFinder}
     * @return the Wordfinder
     */
    public WordFinder getWordFinder() {
        return wordFinder;
    }

    /**
     * Sets the {@link WordFinder}
     * @param wordFinder the WordFinder
     */
    @javax.annotation.Resource(name = "dictionary")
    public void setWordFinder(WordFinder wordFinder) {
        this.wordFinder = wordFinder;
    }
}
