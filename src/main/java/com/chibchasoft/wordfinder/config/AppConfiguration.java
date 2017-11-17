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
package com.chibchasoft.wordfinder.config;

import com.chibchasoft.wordfinder.util.Util;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;

/**
 * The Spring-based configuration for the application
 */
@Configuration
@ComponentScan("com.chibchasoft.wordfinder")
@PropertySource("classpath:config.properties")
public class AppConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(AppConfiguration.class);

    @Value("${letter.points}")
    private String letterPointsJson;

    /**
     * Provides the points assigned to letters used to determine the score of words
     * @return an array of bytes with the points for each letter, indexed by the character position in the range 'a'-'z'
     */
    @Bean(name="letterPoints")
    public byte[] letterPoints() {
        byte[] letterPoints = new byte[Util.LETTERS_SIZE];
        if (letterPointsJson==null) {
            return letterPoints ;
        }

        try {
            JsonObject json = new JsonObject(letterPointsJson);
            for (Map.Entry<String, Object> entry : json.getMap().entrySet()) {
                Byte points = ((Integer) entry.getValue()).byteValue();
                for (char c : entry.getKey().toCharArray()) {
                    letterPoints[c - Util.FIRST_LETTER] = points;
                }
            }
        } catch(DecodeException e) {
            String msg = "An error occurred converting to json object[" + letterPointsJson + "]";
            LOG.warn(msg, e);
            throw e;
        }


        LOG.info("Points for letters loaded");

        return letterPoints;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * Returns a {@link PropertiesFactoryBean} that points the configuration properties for the application
     * @return the {@link PropertiesFactoryBean}
     */
    @Bean(name="applicationProperties")
    public PropertiesFactoryBean applicationProperties() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource("config.properties"));
        return bean;
    }
}