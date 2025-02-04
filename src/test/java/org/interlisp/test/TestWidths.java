/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.test;

import org.interlisp.graphics.FontMetricsExtractor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestWidths {

    private static final String SAMPLE = "All work and no play makes Jack a dull boy! 1234567890\uFFFF\uFFFD";

    private static final char SAMPLE_CHAR = 'A';

    private static final File RESOURCES = new File("src/main/resources");

    private static final int POINT_SIZE = 10;

    static FontMetricsExtractor fme;
    static Collection<FontMetrics> notoSans;
    static Collection<FontMetrics> notoSansDisplay;
    static Collection<FontMetrics> notoSansMono;
    static Collection<FontMetrics> notoSerif;

    @BeforeAll
    static void beforeAll() throws IOException {
        fme = new FontMetricsExtractor();
        notoSans = fme.fromFontDirectory(new File(RESOURCES, "Noto Sans"), POINT_SIZE);
        notoSansDisplay = fme.fromFontDirectory(new File(RESOURCES, "Noto Sans Display"), POINT_SIZE);
        notoSansMono = fme.fromFontDirectory(new File(RESOURCES, "Noto Sans Mono"), POINT_SIZE);
        notoSerif = fme.fromFontDirectory(new File(RESOURCES, "Noto Serif"), POINT_SIZE);
    }

    void checkWidths(Collection<FontMetrics> metrics) {
        metrics.forEach(fm -> {
            final int stringWidth = fm.stringWidth(SAMPLE);
            int totalCharWidth = 0;
            for (int i = 0; i < SAMPLE.length(); i++) {
                totalCharWidth += fm.charWidth(SAMPLE.charAt(i));
            }
            assertEquals(stringWidth, totalCharWidth);
        });
    }

    @Test
    void testCharWidthsEqualStringWidth() {
        checkWidths(notoSans);
        checkWidths(notoSansDisplay);
        checkWidths(notoSansMono);
        checkWidths(notoSerif);
    }
}
