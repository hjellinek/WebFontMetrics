/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io;

import java.io.IOException;
import java.io.Writer;

public class LispNumber implements SExpression {

    private final Number number;

    public LispNumber(Number number) {
        this.number = number;
    }

    @Override
    public void write(Writer w) throws IOException {
        w.write(number.toString());
    }

    @Override
    public String toString() {
        return "LispNumber{" +
                "number=" + number +
                '}';
    }
}
