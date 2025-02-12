/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.tools;

import org.interlisp.io.sexp.CanWriteSExp;
import org.interlisp.io.sexp.LispList;

import java.io.IOException;
import java.io.Writer;

import static org.interlisp.io.sexp.Litatom.atom;

/**
 * Write the metrics in a format the Interlisp function <tt>\HTML.READ-CHARSET-METRICS</tt> can read.
 */
public class WebMetricsWriter {

    /**
     * The file format version.
     * In version 2, the property names are in the keyword package.
     */
    private static final int FORMAT_VERSION = 2;

    private final Writer writer;

    private final CanWriteSExp metrics;

    public WebMetricsWriter(Writer writer, CanWriteSExp metrics) {
        this.writer = writer;
        this.metrics = metrics;
    }

    /**
     * Write the file format version number.
     */
    private void writeFormatVersion() throws IOException {
        new LispList().add(atom(":FORMAT")).add(FORMAT_VERSION).write(writer);
    }

    private void writeMetrics() throws IOException {
        metrics.write(writer);
    }

    /**
     * Write the metrics to the font metrics file for ingestion by <tt>PROCESS-BROWSER-FONT-METRICS</tt>.
     *
     * @throws IOException if there's an I/O problem
     */
    public void writeMetricsFile() throws IOException {
        writeFormatVersion();
        writeMetrics();
    }
}
