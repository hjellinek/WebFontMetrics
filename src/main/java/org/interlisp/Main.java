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

    private static final Logger LOG = LoggerFactory.getLogger(FontMetricsExtractor.class);

    private static final File RESOURCES = new File("src/main/resources");

    private static final char SAMPLE_CHAR = 'A';

    private static class Args {
        @Parameter(names = {"-s", "--size"})
        private float size;
        @Parameter(names = {"-m", "--multiplier"})
        private float multiplier = 1.0f;
    }

    public static void main(String[] args) throws IOException {
        final Args programArgs = new Args();
        JCommander.newBuilder().addObject(programArgs).build().parse(args);

        float pointSize = programArgs.size * programArgs.multiplier;
        final FontMetricsExtractor fme = new FontMetricsExtractor();
        final Collection<FontMetrics> notoSans = fme.fromFontDirectory(new File(RESOURCES, "Noto Sans"), pointSize);
        final Collection<FontMetrics> notoSansDisplay = fme.fromFontDirectory(new File(RESOURCES, "Noto Sans Display"), pointSize);
        final Collection<FontMetrics> notoSansMono = fme.fromFontDirectory(new File(RESOURCES, "Noto Sans Mono"), pointSize);
        final Collection<FontMetrics> notoSerif = fme.fromFontDirectory(new File(RESOURCES, "Noto Serif"), pointSize);

        notoSans.forEach(fm -> {
            final Font font = fm.getFont();
            if (font.isPlain() && font.getFontName().endsWith("Regular")) {
                LOG.info("Font {}({}) width = {}", font.getFontName(), font.getSize(), fm.charWidth(SAMPLE_CHAR));
            }
        });

        LOG.info("done");
    }

}