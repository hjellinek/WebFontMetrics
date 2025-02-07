/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.tools;

import org.interlisp.graphics.FontMetricsExtractor;
import org.interlisp.graphics.FontStack;
import org.interlisp.io.font.CharsetMetricsEntry;
import org.interlisp.io.font.FontMetricsData;
import org.interlisp.io.font.TableOfContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import static java.awt.Font.*;

public class MetricsProcessor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String METRICS_FILE_NAME = "font-metrics.data";

    private static final int[] STYLES = new int[] {PLAIN, BOLD, ITALIC, BOLD + ITALIC};

    private final FontStack stack;

    private final List<Integer> sizes;

    public MetricsProcessor(FontStack stack, List<Integer> sizes) {
        this.stack = stack;
        this.sizes = sizes;
    }

    public void processMetrics() throws IOException {
        final TableOfContents toc = new TableOfContents();
        final FontMetricsData fontMetricsForFile = new FontMetricsData();

        for (int size : sizes) {
            for (int style : STYLES) {
                final FontMetricsExtractor.FontMeasurements lineMeasurements = new FontMetricsExtractor.FontMeasurements();
                // naughty, naughty: we side-effect the lineMeasurements object
                final Collection<CharsetMetricsEntry> allCharsetMetrics = stack.getAllCharsetMetrics(size, style, lineMeasurements);
                fontMetricsForFile.add(stack.getFamilyName(), size, style, lineMeasurements.getHeight(),
                        lineMeasurements.getMaxAscent(), lineMeasurements.getMaxDescent(), allCharsetMetrics);
                toc.add(stack.getFamilyName(), size, style);
            }
        }

        try (final Writer writer = new BufferedWriter(new FileWriter(METRICS_FILE_NAME))) {
            final MetricsFileWriter metricsFileWriter = new MetricsFileWriter(writer, toc, fontMetricsForFile);
            metricsFileWriter.writeMetricsFile();
        }
    }

}
