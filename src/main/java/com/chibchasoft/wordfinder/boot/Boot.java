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
package com.chibchasoft.wordfinder.boot;

import java.util.Properties;

import com.chibchasoft.wordfinder.config.AppConfiguration;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.chibchasoft.vertx.spring.ApplicationContextProvider;
import com.chibchasoft.vertx.verticle.deployment.DependentVerticleDeployer;
import com.chibchasoft.vertx.verticle.deployment.DependentsDeployment;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * Main entry point for the application. This class creates a new Vertx instance,
 * a new Spring annotated-application context and finally deploys the verticles
 */
public class Boot {
    private static final Logger LOG = LoggerFactory.getLogger(Boot.class);

    public Boot() {
    }

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");

        ConfigurableApplicationContext appCtx = new AnnotationConfigApplicationContext(AppConfiguration.class);
        ApplicationContextProvider.setApplicationContext(appCtx);

        final Vertx vertx = Vertx.vertx(new VertxOptions());

        DependentVerticleDeployer dependentVerticleDeployer = new DependentVerticleDeployer();
        dependentVerticleDeployer.setDependentsDeployment(getDependentsDeployment(appCtx));

        vertx.deployVerticle(dependentVerticleDeployer, ar -> {
            if (ar.failed()) {
                LOG.warn("An error occurred while trying to deploy all verticles. " +
                         "At this point the application will exit", ar.cause());
                appCtx.close();
                System.exit(1);
            }
        });
    }

    /**
     * Returns a DependentsDeployment object obtained from the verticles.configuration property
     * @param appCtx The ApplicationContext from which to get the application properties
     * @return The DependentsDeployment object
     */
    private static DependentsDeployment getDependentsDeployment(ApplicationContext appCtx) {
        Properties appProperties = appCtx.getBean("applicationProperties", Properties.class);

        String jsonStr = appProperties.getProperty("verticles.configuration");
        if (jsonStr == null) return null;

        DependentsDeployment dependentsDeployment = new DependentsDeployment();
        try {
            dependentsDeployment.fromJson(new JsonObject(jsonStr));
        } catch(Throwable t) {
            String msg = "Could not decode as JSON=[" + jsonStr + "]";

            LOG.error(msg, t);

            throw new RuntimeException(msg, t);
        }

        return dependentsDeployment;
    }
}