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

import com.chibchasoft.wordfinder.model.Dictionary;
import com.chibchasoft.wordfinder.model.WordFinder;
import io.vertx.test.core.VertxTestBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Test the InitVerticle
 */
public class InitVerticleTest extends VertxTestBase {
    private InitVerticle initVerticle = new InitVerticle();
    private WordFinder wordFinder = new WordFinder() {
        List<String> words = new ArrayList<>();
        @Override
        public WordFinder add(String word) {
            words.add(word);
            return this;
        }

        @Override
        public List<String> getWords(String letters) {
            return words;
        }
    };

    @Before
    public void init() {
        initVerticle.setWordFinder(wordFinder);
    }

    @Test
    public void testNoDictionarySet() {
        initVerticle.setWordFinder(null);

        vertx.deployVerticle(initVerticle, ar -> {
            assertTrue(ar.failed());
            assertTrue(ar.cause().getMessage().contains("WordFinder not set"));
            testComplete();
        });

        await();
    }

    @Test
    public void testNoWordsLocationSet() {
        vertx.deployVerticle(initVerticle, ar -> {
            assertTrue(ar.failed());
            assertTrue(ar.cause().getMessage().contains("wordsLocation[null]"));
            testComplete();
        });

        await();
    }

    @Test
    public void testFailedToReadWordsLocation() {
        initVerticle.setWordsLocation(new ClassPathResource("classpath:bad"));

        vertx.deployVerticle(initVerticle, ar -> {
            assertTrue(ar.failed());
            assertTrue(ar.cause().getMessage().contains("wordsLocation[class path resource"));
            testComplete();
        });

        await();
    }

    @Test
    public void testFailedWordsLocationIOException() {
        InputStream is = new InputStream() {

            @Override
            public int read() throws IOException {
                throw new IOException("ioexception");
            }
        };

        initVerticle.setWordsLocation(new InputStreamResource(is));

        vertx.deployVerticle(initVerticle, ar -> {
            assertTrue(ar.failed());
            assertTrue(ar.cause().getMessage().contains("ioexception"));
            testComplete();
        });

        await();
    }

    @Test
    public void testLoadSuccessful() {
        ByteArrayResource bar = new ByteArrayResource("hello\nworld\n".getBytes());
        initVerticle.setWordsLocation(bar);

        vertx.deployVerticle(initVerticle, ar -> {
            assertTrue(ar.succeeded());
            assertTrue(initVerticle.getWordFinder().getWords("helloworld").contains("hello"));
            assertTrue(initVerticle.getWordFinder().getWords("helloworld").contains("world"));
            testComplete();
        });

        await();
    }
}
