/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io;

import java.io.IOException;
import java.io.Writer;

public class LispString implements SExpression {

    private final String str;

    public LispString(String str) {
        this.str = str;
    }

    /**
     * A shorthand way to create a {@link LispString}.
     *
     * @param s the string
     * @return the resulting {@link LispString}
     */
    public static LispString str(String s) {
        return new LispString(s);
    }

    @Override
    public void write(Writer w) throws IOException {
        w.write('"');
        for (int i = 0; i < str.length(); i++) {
            final char ch = str.charAt(i);
            if (ch == '"') {
                w.write("\\");
            }
            w.write(ch);
        }
        w.write('"');
    }

    @Override
    public String toString() {
        return "LispString{" +
                "str='" + str + '\'' +
                '}';
    }
}
