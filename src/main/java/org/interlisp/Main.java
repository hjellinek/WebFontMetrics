package org.interlisp;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.interlisp.graphics.FontMetricsExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/*
 * Download Web fonts and collect their metrics.
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
public class Main {

    private static class Args {
        @Parameter(names = {"-s", "--size"})
        private float size;
        @Parameter(names = {"-m", "--multiplier"})
        private float multiplier = 1.0f;
    }

    private static final Logger log = LoggerFactory.getLogger(FontMetricsExtractor.class);

    private static final File resources = new File("src/main/resources");

    private static final String SAMPLE = "All work and no play makes Jack a dull boy! 1234567890\uFFFF\uFFFD";

    private static void checkWidths(Collection<FontMetrics> metrics) {
        metrics.forEach(fm -> {
            final int stringWidth = fm.stringWidth(SAMPLE);
            int totalCharWidth = 0;
            for (int i = 0; i < SAMPLE.length(); i++) {
                totalCharWidth += fm.charWidth(SAMPLE.charAt(i));
            }
            if (stringWidth != totalCharWidth) {
                final Font font = fm.getFont();
                log.warn("{} {}: chars = {}, string = {}", font.getFamily(), font.getFontName(), totalCharWidth, stringWidth);
            }
        });
    }

    public static void main(String[] args) throws IOException {
        final Args programArgs = new Args();
        JCommander.newBuilder().addObject(programArgs).build().parse(args);

        float pointSize = programArgs.size * programArgs.multiplier;
        final FontMetricsExtractor fme = new FontMetricsExtractor();
        final Collection<FontMetrics> notoSans = fme.fromFontDirectory(new File(resources, "Noto Sans"), pointSize);
        final Collection<FontMetrics> notoSansDisplay = fme.fromFontDirectory(new File(resources, "Noto Sans Display"), pointSize);
        final Collection<FontMetrics> notoSansMono = fme.fromFontDirectory(new File(resources, "Noto Sans Mono"), pointSize);
        final Collection<FontMetrics> notoSerif = fme.fromFontDirectory(new File(resources, "Noto Serif"), pointSize);

        checkWidths(notoSans);
        checkWidths(notoSansDisplay);
        checkWidths(notoSansMono);
        checkWidths(notoSerif);

        log.info("done");
    }

}