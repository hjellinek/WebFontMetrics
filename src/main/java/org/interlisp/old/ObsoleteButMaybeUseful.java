/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.old;

import org.interlisp.graphics.FontMetricsExtractor;
import org.interlisp.graphics.FontStack;
import org.interlisp.graphics.FontUtils;
import org.interlisp.graphics.WebFontDownloader;
import org.interlisp.io.sexp.LispList;
import org.interlisp.unicode.XccsToUnicode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;

import static org.interlisp.io.sexp.LispNil.NIL;

public class ObsoleteButMaybeUseful {

    private static final Logger LOG = LoggerFactory.getLogger(ObsoleteButMaybeUseful.class);

    /**
     * Log the percent of each charset that's displayable using the given {@link FontStack}.
     *
     * @param xccsToUnicode the XCCS to Unicode mapper
     * @param fontStack the font stack
     */
    private static void showCoverage(XccsToUnicode xccsToUnicode, FontStack fontStack) {
        final Map<Integer, Float> fractionMissingPerCharset = new HashMap<>();
        for (int charset : xccsToUnicode.charsets()) {
            final SortedSet<Integer> xccsCodesInCharset = xccsToUnicode.charsetMembers(charset);
            final float numXccsCodesInCharset = xccsCodesInCharset.size();
            int notDisplayableCount = 0;
            for (int xccsCode : xccsCodesInCharset) {
                int unicode = xccsToUnicode.unicode(xccsCode);
                if (!fontStack.isDisplayableByAny((char)unicode)) {
                    notDisplayableCount++;
                }
            }
            fractionMissingPerCharset.put(charset, notDisplayableCount / numXccsCodesInCharset);
        }

        LOG.info("The charsets containing chars that won't display, with percent displayable:");
        LOG.info("Stack: {}", fontStack);
        fractionMissingPerCharset.forEach((charset, fraction) -> LOG.info("0x{} (#o{}) ({}) {}: {}%",
                Integer.toHexString(charset).toUpperCase(), Integer.toOctalString(charset), charset,
                xccsToUnicode.charsetName(charset), (int)(100 - fraction * 100)));
    }

    private static void writeWidths(float pointSize, XccsToUnicode xccsToUnicode, Writer writer)
            throws IOException, URISyntaxException, FontFormatException {
        final WebFontDownloader wfd = new WebFontDownloader("some font URL");
        final List<Font> scaledFonts = wfd.getFonts().stream().map(f -> f.deriveFont(pointSize)).toList();

        final FontMetricsExtractor fme = new FontMetricsExtractor();

        for (FontMetrics fontMetrics : fme.fromFonts(scaledFonts)) {
            final Font font = fontMetrics.getFont();
            int fontSize = font.getSize();
            int fontAscent = fontMetrics.getAscent();
            int fontDescent = fontMetrics.getDescent();
            int fontHeight = fontMetrics.getHeight();
            for (int charset : xccsToUnicode.charsets()) {
                float totalWidth = 0;
                int numDisplayableChars = 0;
                final LispList charsetRecord = new LispList();
                final SortedSet<Integer> xccsCodes = xccsToUnicode.charsetMembers(charset);
                for (int xccs = charset << 8; xccs <= (charset << 8) + 0xFF; xccs++) {
                    if (xccsCodes.contains(xccs)) {
                        final Integer unicode = xccsToUnicode.unicode(xccs);
                        if (unicode == null || !font.canDisplay(unicode)) {
                            charsetRecord.add(NIL);
                        } else {
                            final float width = FontUtils.pointsToCssPixels(fontMetrics.charWidth(unicode));
                            charsetRecord.add(width);
                            totalWidth += width;
                            numDisplayableChars++;
                        }
                    } else {
                        charsetRecord.add(NIL);
                    }
                }

                final float avgWidth = totalWidth / numDisplayableChars;
                charsetRecord.write(writer);
                writer.write('\n');
                writer.flush();
            }
        }
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

    private static void census(float pointSize) throws IOException, URISyntaxException, FontFormatException {
        final XccsToUnicode xccsToUnicode = XccsToUnicode.getInstance();

        final FontMetricsExtractor fme = new FontMetricsExtractor();
        final Collection<FontMetrics> notoSans = fme.fromFontDirectory(new File(new File("some resource dir"), "Noto Sans"), pointSize);
        final Collection<FontMetrics> notoSansDisplay = fme.fromFontDirectory(new File(new File("some resource dir"), "Noto Sans Display"), pointSize);
        final Collection<FontMetrics> notoSansMono = fme.fromFontDirectory(new File(new File("some resource dir"), "Noto Sans Mono"), pointSize);
        final Collection<FontMetrics> notoSerif = fme.fromFontDirectory(new File(new File("some resource dir"), "Noto Serif"), pointSize);

        LOG.info("----- Files -----");
        fontCensus(notoSans, xccsToUnicode);
        fontCensus(notoSansDisplay, xccsToUnicode);
        fontCensus(notoSansMono, xccsToUnicode);
        fontCensus(notoSerif, xccsToUnicode);

        LOG.info("----- Web -----");
        final WebFontDownloader wfd = new WebFontDownloader("some font URL");
        fontCensus(fme.fromFonts(wfd.getFonts()), xccsToUnicode);

        LOG.info("done");
    }
}
