/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io.sexp;

import java.io.IOException;
import java.io.Writer;

public class Litatom implements SExpression {

    private final String str;

    public Litatom(String str) {
        this.str = str;
    }

    /**
     * A shorthand way to create a {@link Litatom} from a string.
     *
     * @param s the atom's package and p-name.
     * @return the {@link Litatom}
     */
    public static Litatom atom(String s) {
        return new Litatom(s);
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