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
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Vertx verticle that listens for requests to find words via event bus messages.
 */
@Component("findWordsVerticle")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FindWordsVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(FindWordsVerticle.class);

    private String busAddress = "findWords";

    private WordFinder wordFinder;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        if (wordFinder == null) {
            String msg = "WordFinder not set";
            LOG.warn(msg);
            startFuture.fail(msg);
            return;
        }

        if (busAddress == null) {
            String msg = "BusAddress not set";
            LOG.warn(msg);
            startFuture.fail(msg);
            return;
        }

        vertx.eventBus().consumer(busAddress, this::findWords);

        startFuture.complete();
    }

    /**
     * Replies to the messages with all the possible words that can be created using the
     * letter provided in the message
     * @param msg The message which includes the letters
     */
    public void findWords(Message<String> msg) {
        String letters = msg.body();
        LOG.info("Request to find words for " + letters);

        List<String> words = wordFinder.getWords(letters);

        LOG.debug("Found " + words);

        JsonArray jsonResults = new JsonArray();
        words.forEach(jsonResults::add);

        msg.reply(jsonResults);
    }

    /**
     * Gets the event bus address to listen for requests to find words. Defaults to findWords
     * @return the event bus address.
     */
    public String getBusAddress() {
        return busAddress;
    }

    /**
     * Sets the event bus address to listen for requests to find words
     * @param busAddress the event bus address
     */
    @Value("${vertx.findwords.address:findWords}")
    public void setBusAddress(String busAddress) {
        this.busAddress = busAddress;
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
    @Resource(name = "dictionary")
    public void setWordFinder(WordFinder wordFinder) {
        this.wordFinder = wordFinder;
    }
}
