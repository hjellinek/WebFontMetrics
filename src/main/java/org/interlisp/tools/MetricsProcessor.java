/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.tools;

import org.interlisp.graphics.FontStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MetricsProcessor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String METRICS_FILE_NAME = "font-metrics.data";

    private final List<FontStack> stack;

    private final List<Integer> sizes;

    public MetricsProcessor(List<FontStack> stack, List<Integer> sizes) {
        this.stack = stack;
        this.sizes = sizes;
    }

    public void processMetrics() {

    }

}
