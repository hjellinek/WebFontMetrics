/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.tools;

import org.interlisp.io.LispList;
import org.interlisp.io.font.Metrics;
import org.interlisp.io.font.TableOfContents;

import java.io.IOException;
import java.io.Writer;

import static org.interlisp.io.Litatom.atom;

/**
 * Write the metrics in a format the Interlisp function <tt>PROCESS-BROWSER-FONT-METRICS</tt> can understand.
 */
public class MetricsFileWriter {

    /**
     * The file format version.
     * In version 2, the property names are in the keyword package.
     */
    private static final int VERSION = 2;

    private final Writer writer;

    private final TableOfContents toc;

    private final Metrics metrics;

    public MetricsFileWriter(Writer writer, TableOfContents toc, Metrics metrics) {
        this.writer = writer;
        this.toc = toc;
        this.metrics = metrics;
    }

    /**
     * Write the file format version number.
     */
    public void writeFormatVersion() throws IOException {
        new LispList().add(atom(":VERSION")).add(VERSION).write(writer);
    }

    /**
     * Write the table of contents.
     */
    private void writeToc() throws IOException {
        toc.write(writer);
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
        writeToc();
        writeMetrics();
    }
}
