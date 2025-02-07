/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io.sexp;

import java.io.IOException;
import java.io.Writer;

/**
 * An object that can write itself to a {@link java.io.Writer} as a Lisp s-expression.
 */
public interface SExpression {

    void write(Writer w) throws IOException;

}
