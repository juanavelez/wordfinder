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
import io.vertx.test.core.VertxTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Test the FindWordsVerticle
 */
public class FindWordsVerticleTest extends VertxTestBase {
    private FindWordsVerticle findWordsVerticle = new FindWordsVerticle();
    private WordFinder wordFinder = new WordFinder() {
        @Override
        public WordFinder add(String word) {
            return this;
        }

        @Override
        public List<String> getWords(String letters) {
            return Collections.emptyList();
        }
    };

    @Before
    public void init() {
        findWordsVerticle.setWordFinder(wordFinder);
    }

    @Test
    public void testNoWordFinderSet() {
        findWordsVerticle.setWordFinder(null);

        vertx.deployVerticle(findWordsVerticle, ar -> {
            assertTrue(ar.failed());
            assertTrue(ar.cause().getMessage().contains("WordFinder not set"));
            testComplete();
        });

        await();
    }

    @Test
    public void testNoBusAddressSet() {
        findWordsVerticle.setBusAddress(null);

        vertx.deployVerticle(findWordsVerticle, ar -> {
            assertTrue(ar.failed());
            assertTrue(ar.cause().getMessage().contains("BusAddress not set"));
            testComplete();
        });

        await();
    }

    @Test
    public void testWordFinderSet() {
        vertx.deployVerticle(findWordsVerticle, ar -> {
            assertTrue(ar.succeeded());
            testComplete();
        });

        await();
    }

    @Test
    public void testFindWords() {
        WordFinder wf = new WordFinder() {
            String word;
            @Override
            public WordFinder add(String word) {
                this.word = word;
                return this;
            }

            @Override
            public List<String> getWords(String letters) {
                return Arrays.asList(word);
            }
        };

        wf.add("he");

        vertx.deployVerticle(findWordsVerticle, ar -> {
            assertTrue(ar.succeeded());
            vertx.eventBus().send(findWordsVerticle.getBusAddress(), "hello", arm -> {
                assertTrue(ar.succeeded());
                assertTrue(ar.result().equals("he"));
            });
            testComplete();
        });

        await();
    }
}
