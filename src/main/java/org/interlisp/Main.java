package org.interlisp;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.interlisp.graphics.FontMetricsExtractor;
import org.interlisp.graphics.WebFontDownloader;
import org.interlisp.unicode.XccsToUnicode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

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

    private static final String ALL_FONTS_URL = "https://fonts.googleapis.com/css2?family=Noto+Sans&"+
            "family=Noto+Sans+Display&"+
            "family=Noto+Sans+Mono&"+
            "family=Noto+Serif";

    private static class Args {
        @Parameter(names = {"-s", "--size"})
        private float size;
        @Parameter(names = {"-m", "--multiplier"})
        private float multiplier = 1.0f;
    }

    public static void main(String[] args) throws IOException, URISyntaxException, FontFormatException {
        final Args programArgs = new Args();
        JCommander.newBuilder().addObject(programArgs).build().parse(args);

        final XccsToUnicode xccsToUnicode = new XccsToUnicode(new File(RESOURCES, "data"));

        float pointSize = programArgs.size * programArgs.multiplier;
        final FontMetricsExtractor fme = new FontMetricsExtractor();
        final Collection<FontMetrics> notoSans = fme.fromFontDirectory(new File(RESOURCES, "Noto Sans"), pointSize);
        final Collection<FontMetrics> notoSansDisplay = fme.fromFontDirectory(new File(RESOURCES, "Noto Sans Display"), pointSize);
        final Collection<FontMetrics> notoSansMono = fme.fromFontDirectory(new File(RESOURCES, "Noto Sans Mono"), pointSize);
        final Collection<FontMetrics> notoSerif = fme.fromFontDirectory(new File(RESOURCES, "Noto Serif"), pointSize);

        LOG.info("----- Files -----");
        fontCensus(notoSans, xccsToUnicode);
        fontCensus(notoSansDisplay, xccsToUnicode);
        fontCensus(notoSansMono, xccsToUnicode);
        fontCensus(notoSerif, xccsToUnicode);

        LOG.info("----- Web -----");
        final WebFontDownloader wfd = new WebFontDownloader(ALL_FONTS_URL);
        fontCensus(fme.fromFonts(wfd.getFonts()), xccsToUnicode);

        LOG.info("done");
    }

    private static void fontCensus(Collection<FontMetrics> metricsCollection, XccsToUnicode xccsToUnicode) {
        metricsCollection.forEach(fm -> {
            final Font font = fm.getFont();
            if (font.isPlain() && font.getFontName().endsWith("Regular")) {
                final SortedSet<Integer> displayableCharsets = new TreeSet<>();
                for (int xccs = 0; xccs <= 0xFFFF; xccs++) {
                    final Integer unicode = xccsToUnicode.unicode(xccs);
                    if (unicode != null && font.canDisplay(unicode)) {
                        displayableCharsets.add(XccsToUnicode.charset(xccs));
                    }
                }
                LOG.info("{} can display {} charsets", font.getFontName(), displayableCharsets.size());
            }

        });
    }

}