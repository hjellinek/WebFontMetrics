/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.tools;

import org.interlisp.io.LispList;
import org.interlisp.io.SExpression;
import org.interlisp.io.TableOfContents;

import java.io.IOException;
import java.io.Writer;

import static org.interlisp.io.Litatom.atom;

public class MetricsExtractor {

    /**
     * The file format version.
     * In version 2, the property names are in the keyword package.
     */
    private static final int VERSION = 2;

    private final Writer writer;

    public MetricsExtractor(Writer writer) {
        this.writer = writer;
    }

    /**
     * Write the file format version number.
     */
    public void writeFormatVersion() throws IOException {
        new LispList().add(atom(":VERSION")).add(VERSION).write(writer);
    }

    /**
     * Write the table of contents.
     *
     * @param toc the table of contents
     */
    public void writeToc(TableOfContents toc) throws IOException {
        toc.write(writer);
    }

    public void writeMetrics(SExpression metrics) {
        // TODO write this next!
    }
}
