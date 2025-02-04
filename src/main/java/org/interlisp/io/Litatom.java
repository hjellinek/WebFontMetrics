/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io;

import java.io.IOException;
import java.io.Writer;

public class Litatom implements SExpression {

    private final String str;

    public Litatom(String str) {
        this.str = str;
    }

    @Override
    public void write(Writer w) throws IOException {
        w.write(str);
    }

    @Override
    public String toString() {
        return "Litatom{" +
                "str='" + str + '\'' +
                '}';
    }
}