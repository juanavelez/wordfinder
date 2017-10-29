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
package com.chibchasoft.wordfinder.model;

/**
 * A pair of objects. The pair itself is immutable.
 *
 * @param <T1> The type of the first object
 * @param <T2> The type of the second object
 */
public class Pair<T1, T2> {
    private final T1 first;
    private final T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first object
     * @return the first object
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * Returns the second object
     * @return the second object
     */
    public T2 getSecond() {
        return second;
    }
}
