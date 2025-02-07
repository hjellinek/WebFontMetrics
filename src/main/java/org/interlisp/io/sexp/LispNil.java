/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io.sexp;

import java.io.IOException;
import java.io.Writer;

public class LispNil implements SExpression {

    public static LispNil NIL = new LispNil();

    public LispNil() {
    }

    @Override
    public void write(Writer w) throws IOException {
        w.write("NIL");
    }
}
