/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io.sexp;

import java.io.IOException;
import java.io.Writer;

/**
 * An s-expression wrapper for a number.
 */
public class LispNum implements SExpression {

    final Number num;

    public LispNum(Number num) {
        this.num = num;
    }

    /**
     * A shorthand way to create a {@link LispNum}.
     *
     * @param num the number
     * @return the {@link LispNum}
     */
    public static LispNum num(Number num) {
        return new LispNum(num);
    }

    @Override
    public void write(Writer w) throws IOException {
        w.write(num.toString());
    }
}
