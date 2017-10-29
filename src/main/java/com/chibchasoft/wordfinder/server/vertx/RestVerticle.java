package com.chibchasoft.wordfinder.server.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This vertx verticle is the server for the REST API.
 */
@Component("restVerticle")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RestVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(RestVerticle.class);

    private String serverHost = "localhost";
    private int serverPort = 8080;

    private HttpServer server;

    private String busAddress = "findWords";

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        HttpServerOptions serverOptions = new HttpServerOptions();
        serverOptions.setHost(serverHost);
        serverOptions.setPort(serverPort);

        server = vertx.createHttpServer(serverOptions);

        Router router = Router.router(vertx);
        router.route("/words/:letters" ).handler(this::findWord).produces("application/json");

        server.requestHandler(router::accept).listen(ar -> {
            if (ar.failed()) {
                LOG.warn("An error occurred while trying to start the server to listen for connections", ar.cause());

                startFuture.fail(ar.cause());
            } else {
                startFuture.complete();
            }
        });
    }

    @Override
    public void stop() {
        if (server != null) {
            server.close();
        }
    }

    /**
     * Processes a request to find words using the provided letters
     * @param ctx the routing context
     */
    protected void findWord(RoutingContext ctx) {
        HttpServerResponse response = ctx.response();

        String letters = ctx.request().getParam("letters");

        vertx.eventBus().<JsonArray>send(busAddress, letters, ar-> {
            if (ar.failed()) {
                LOG.warn("An error occurred while waiting for the reply to find words", ar.cause());

                response.setStatusCode(500);
                response.end();
            } else {
                response.putHeader("content-type", "application/json");

                JsonArray words = ar.result().body();

                response.end(words.encode());
            }
        });
    }

    /**
     * Gets the host used for the server. Defaults to localhost
     * @return the host.
     */
    public String getServerHost() {
        return serverHost;
    }

    /**
     * Sets the host used for the server. Defaults to localhost
     * @param serverHost the host.
     */
    @Value("${rest.server.host:localhost}")
    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    /**
     * Gets the port used for the server. Defaults to 8080.
     * @return the port.
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Sets the port used for the server.the port used for the server. Defaults to 8080
     * @param serverPort the port.
     */
    @Value("${rest.server.port:8080}")
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Gets the event bus address where requests to find the words is sent. Defaults to findWords
     * @return the event bus address.
     */
    public String getBusAddress() {
        return busAddress;
    }

    /**
     * Sets the event bus address where requests to find the words is sent.
     * @param busAddress the event bus address
     */
    @Value("${vertx.findwords.address:findWords}")
    public void setBusAddress(String busAddress) {
        this.busAddress = busAddress;
    }
}
