/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.tools;

import org.interlisp.graphics.FontMetricsExtractor;
import org.interlisp.graphics.FontStack;
import org.interlisp.graphics.FontUtils;
import org.interlisp.io.font.ConvertToLisp;
import org.interlisp.io.font.WebCharsetMetrics;
import org.interlisp.io.font.WebFontDescr;
import org.interlisp.unicode.XccsToUnicode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import static java.awt.Font.*;

public class MetricsProcessor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String WEB_FONT_METRICS_EXT = "wfm";

    public static final String WEB_CHARSET_METRICS_EXT = "wcm";

    private static final int[] STYLES = new int[]{PLAIN, BOLD, ITALIC, BOLD + ITALIC};

    public static final String NO_EXPANSION = "REGULAR";

    private final XccsToUnicode xccsToUnicode = XccsToUnicode.getInstance();

    private final ConvertToLisp cvt = new ConvertToLisp();

    private final FontStack stack;

    private final List<Integer> sizes;

    private final File dir;

    private final int fontScale;

    public MetricsProcessor(File dir, FontStack stack, int fontScale, List<Integer> sizes) {
        this.dir = dir;
        this.stack = stack;
        this.fontScale = fontScale;
        this.sizes = sizes;
    }

    /**
     * Return the name of the file that holds the metrics for the given font.
     *
     * @param family    the name of the font family
     * @param size      the font size in points
     * @param weight    the font's weight
     * @param slope     its slope
     * @param expansion its expansion
     * @return the file name
     */
    private String makeLispFontMetricsFileName(String family, int size, String weight,
                                               String slope, String expansion) {
        return String.format("%s-%d-%s-%s-%s.%s", cvt.makeLispFamilyNameStr(family),
                size, weight, slope, expansion, WEB_FONT_METRICS_EXT);
    }

    /**
     * Return the name of the file that holds the metrics for the given charset of the font.
     *
     * @param family     the name of the font family
     * @param size       the font size in points
     * @param weight     the font's weight
     * @param slope      its slope
     * @param expansion  its expansion
     * @param charsetNum the character set number
     * @return the file name
     */
    private String makeLispCharsetMetricsFileName(String family, int size, String weight,
                                                  String slope, String expansion, int charsetNum) {
        return String.format("%s-%d-%s-%s-%s-%d.%s", cvt.makeLispFamilyNameStr(family),
                size, weight, slope, expansion, charsetNum, WEB_CHARSET_METRICS_EXT);
    }

    /**
     * Write the font and charset metrics files for the stack.
     *
     * @throws IOException if there's an I/O problem
     */
    public void writeStackMetrics() throws IOException {

        for (int size : sizes) {
            final int scaledFontSize = size * fontScale;

            for (int style : STYLES) {
                final FontMetricsExtractor.FontMeasurements lineMeasurements = new FontMetricsExtractor.FontMeasurements();

                final String familyName = stack.getFamilyName();
                final String weight = FontUtils.weight(style);
                final String slope = FontUtils.slope(style);

                // naughty, naughty: we side-effect the lineMeasurements object
                final List<WebCharsetMetrics> allCharsetMetrics =
                        stack.getAllCharsetMetrics(scaledFontSize, style, lineMeasurements);
                for (WebCharsetMetrics wcm : allCharsetMetrics) {
                    final String webMetricsFileName = makeLispCharsetMetricsFileName(familyName, size, weight, slope, NO_EXPANSION, wcm.charset());
                    try (final Writer writer = new FileWriter(new File(dir, webMetricsFileName))) {
                        final WebMetricsWriter charsetMetricsFileWriter = new WebMetricsWriter(writer, wcm);
                        charsetMetricsFileWriter.writeMetricsFile();
                    }
                }

                final WebCharsetMetrics charset0Metrics = allCharsetMetrics.getFirst();

                // base the font ascent, descent, and height on the charset 0 metrics
                final WebFontDescr fontMetricsForFile =
                        new WebFontDescr(familyName, size, charset0Metrics.maxHeight(),
                                style, charset0Metrics.maxAscent(), charset0Metrics.maxDescent(),
                                lineMeasurements.getSlugWidth(), xccsToUnicode.charsets());

                final String fontMetricsFileName =
                        makeLispFontMetricsFileName(familyName, size, weight, slope, NO_EXPANSION);
                try (final Writer writer = new FileWriter(new File(dir, fontMetricsFileName))) {
                    final WebMetricsWriter fontMetricsFileWriter = new WebMetricsWriter(writer, fontMetricsForFile);
                    fontMetricsFileWriter.writeMetricsFile();
                }
            }
        }
    }

}
