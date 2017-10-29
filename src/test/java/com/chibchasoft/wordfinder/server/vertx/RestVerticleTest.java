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
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.test.core.VertxTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the RestVerticle
 */
public class RestVerticleTest extends VertxTestBase {
    private RestVerticle restVerticle = new RestVerticle();
    private HttpClient client;

    @Before
    public void init() {
    }

    @After
    public void after() {
        if (client != null)
            client.close();

        restVerticle.stop();
    }

    @Test
    public void testDefaults() {
        createConsumer(restVerticle.getBusAddress(), msg -> {
            msg.reply(new JsonArray());
        });

        vertx.deployVerticle(restVerticle, ar -> {
            assertTrue(ar.succeeded());
            client = createHttpCient(restVerticle.getServerHost(), restVerticle.getServerPort());
            client.getNow("/words/x", resp -> {
                assertTrue(resp.statusCode() == 200);
                resp.bodyHandler(buf -> {
                    JsonArray jsonArray = new JsonArray(buf);
                    assertTrue(jsonArray.isEmpty());
                    testComplete();
                });
            });
        });

        await();
    }

    @Test
    public void testPort8090() {
        restVerticle.setServerPort(8090);

        createConsumer(restVerticle.getBusAddress(), msg -> {
            msg.reply(new JsonArray());
        });

        vertx.deployVerticle(restVerticle, ar -> {
            assertTrue(ar.succeeded());
            client = createHttpCient(restVerticle.getServerHost(), restVerticle.getServerPort());
            client.getNow("/words/x", resp -> {
                assertTrue(resp.statusCode() == 200);
                resp.bodyHandler(buf -> {
                    JsonArray jsonArray = new JsonArray(buf);
                    assertTrue(jsonArray.isEmpty());
                    testComplete();
                });
            });
        });

        await();
    }

    @Test
    public void testPort80() {
        restVerticle.setServerPort(80);

        createConsumer(restVerticle.getBusAddress(), msg -> {
            msg.reply(new JsonArray());
        });

        vertx.deployVerticle(restVerticle, ar -> {
            assertTrue(ar.failed());
            assertTrue(ar.cause().getMessage().contains("Permission denied"));
            testComplete();
        });

        await();
    }

    @Test
    public void testHostAllZeros() {
        restVerticle.setServerHost("0.0.0.0");

        createConsumer(restVerticle.getBusAddress(), msg -> {
            msg.reply(new JsonArray());
        });

        vertx.deployVerticle(restVerticle, ar -> {
            assertTrue(ar.succeeded());
            client = createHttpCient(restVerticle.getServerHost(), restVerticle.getServerPort());
            client.getNow("/words/x", resp -> {
                assertTrue(resp.statusCode() == 200);
                resp.bodyHandler(buf -> {
                    JsonArray jsonArray = new JsonArray(buf);
                    assertTrue(jsonArray.isEmpty());
                    testComplete();
                });
            });
        });

        await();
    }

    @Test
    public void testDifferentBusAddress() {
        restVerticle.setBusAddress("hello");

        createConsumer(restVerticle.getBusAddress(), msg -> {
            msg.reply(new JsonArray());
        });

        vertx.deployVerticle(restVerticle, ar -> {
            assertTrue(ar.succeeded());
            client = createHttpCient(restVerticle.getServerHost(), restVerticle.getServerPort());
            client.getNow("/words/x", resp -> {
                assertTrue(resp.statusCode() == 200);
                resp.bodyHandler(buf -> {
                    JsonArray jsonArray = new JsonArray(buf);
                    assertTrue(jsonArray.isEmpty());
                    testComplete();
                });
            });
        });

        await();
    }

    @Test
    public void testNoLettersInPath() {
        createConsumer(restVerticle.getBusAddress(), msg -> {
            msg.reply(new JsonArray());
        });

        vertx.deployVerticle(restVerticle, ar -> {
            assertTrue(ar.succeeded());
            client = createHttpCient(restVerticle.getServerHost(), restVerticle.getServerPort());
            client.getNow("/words/", resp -> {
                assertTrue(resp.statusCode() == 404);
                testComplete();
            });
        });

        await();
    }

    @Test
    public void testBadPath() {
        vertx.deployVerticle(restVerticle, ar -> {
            assertTrue(ar.succeeded());
            client = createHttpCient(restVerticle.getServerHost(), restVerticle.getServerPort());
            client.getNow("/bad/x", resp -> {
                assertTrue(resp.statusCode() == 404);
                testComplete();
            });
        });

        await();
    }

    @Test
    public void testFailedReply() {
        createConsumer(restVerticle.getBusAddress(), msg -> {
            msg.fail(100, "test");
        });

        vertx.deployVerticle(restVerticle, ar -> {
            assertTrue(ar.succeeded());
            client = createHttpCient(restVerticle.getServerHost(), restVerticle.getServerPort());
            client.getNow("/words/x", resp -> {
                assertTrue(resp.statusCode() == 500);
                testComplete();
            });
        });

        await();
    }

    private HttpClient createHttpCient(String host, int port) {
        HttpClientOptions httpClientOptions = new HttpClientOptions();
        httpClientOptions.setDefaultPort(port);
        httpClientOptions.setDefaultHost(host);
        return vertx.createHttpClient(httpClientOptions);
    }

    private void createConsumer(String address, Handler<Message<String>> handler) {
        vertx.eventBus().consumer(address, handler);
    }
}
