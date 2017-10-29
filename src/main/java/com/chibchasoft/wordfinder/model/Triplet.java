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
 * A triplet of objects. The triplet itself is immutable.
 *
 * @param <T1> The type of the first object
 * @param <T2> The type of the second object
 * @param <T3> The type of the third object
 */
public class Triplet<T1, T2, T3> extends Pair<T1, T2> {
    private final T3 third;

    public Triplet(T1 first, T2 second, T3 third) {
        super(first, second);
        this.third = third;
    }

    /**
     * Returns the third object
     * @return the third object
     */
    public T3 getThird() {
        return third;
    }
}
