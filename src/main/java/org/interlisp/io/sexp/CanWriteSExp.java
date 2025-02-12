/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io.sexp;

import java.io.IOException;
import java.io.Writer;

/**
 * An interface marking a class than can write itself to a {@link Writer} as an
 * s-expression.
 */
public interface CanWriteSExp {

    /**
     * Write this as an s-expression.
     *
     * @param writer the {@link Writer} we're writing to
     * @throws IOException if there's an I/O problem
     */
    void write(Writer writer) throws IOException;

}
