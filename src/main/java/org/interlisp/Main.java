package org.interlisp;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.interlisp.graphics.FontMetricsExtractor;
import org.interlisp.graphics.FontStack;
import org.interlisp.graphics.FontUtils;
import org.interlisp.graphics.WebFontDownloader;
import org.interlisp.io.LispList;
import org.interlisp.unicode.XccsToUnicode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.interlisp.io.LispNil.NIL;

/*
 * Download Web fonts and write their metrics to files suitable for use by Medley Interlisp's
 * <tt>HTMLSTREAM</tt>.
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(FontMetricsExtractor.class);

    private static final File RESOURCES = new File("src/main/resources");

    private static final char SAMPLE_CHAR = 'A';

    private static final String NOTO_SANS_URL = "https://fonts.googleapis.com/css2?family=Noto+Sans";

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

        final Writer writer = new BufferedWriter(new OutputStreamWriter(System.out));

        final XccsToUnicode xccsToUnicode = new XccsToUnicode(new File(RESOURCES, "data"));

        float pointSize = programArgs.size * programArgs.multiplier;

        final FontStack notoSans = new FontStack("Noto Sans", "Noto Sans",
                "Noto Sans Simplified Chinese", "Noto Sans Traditional Chinese", "Noto Sans Japanese", "Noto Sans Korean",
                "Noto Sans Arabic", "Noto Sans Hebrew", "Noto Sans Runic",
                "Noto Sans Georgian", "Noto Sans Armenian",
                "Noto Sans Math", "Noto Sans Symbols", "Noto Sans Symbols 2");
        final FontStack notoSansMono = new FontStack("Noto Sans Mono", "Noto Sans Mono");
        final FontStack notoSansDisplay = new FontStack("Noto Sans Display", "Noto Sans Display");
        final FontStack notoSerif = new FontStack("Noto Serif", "Noto Serif",
                "Noto Serif Simplified Chinese", "Noto Serif Traditional Chinese", "Noto Serif Japanese", "Noto Serif Korean",
                "Noto Serif Hebrew", "Noto Serif Georgian", "Noto Serif Armenian");

        final SortedSet<Integer> charsetsContainingCharsThatWontDisplay = new TreeSet<>();
        final SortedSet<Integer> charsThatWontDisplay = new TreeSet<>();
        for (int charset : xccsToUnicode.charsets()) {
            for (int xccsCode : xccsToUnicode.charsetMembers(charset)) {
                int unicode = xccsToUnicode.unicode(xccsCode);
                if (!notoSans.isDisplayableByAny((char)unicode)) {
                    charsetsContainingCharsThatWontDisplay.add(charset);
                    charsThatWontDisplay.add(xccsCode);
                    break;
                }
            }
        }

        LOG.info("There are {} charsets containing chars that won't display.",
                charsetsContainingCharsThatWontDisplay.size());
        LOG.info("The charsets containing chars that won't display are:");
        charsetsContainingCharsThatWontDisplay.forEach(charset -> LOG.info("0x{} (#o{}) ({}): {}",
                Integer.toHexString(charset).toUpperCase(), Integer.toOctalString(charset), charset,
                xccsToUnicode.charsetName(charset)));

    }

    private static void writeWidths(float pointSize, XccsToUnicode xccsToUnicode, Writer writer)
            throws IOException, URISyntaxException, FontFormatException {
        final WebFontDownloader wfd = new WebFontDownloader(NOTO_SANS_URL);
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
        final XccsToUnicode xccsToUnicode = new XccsToUnicode(new File(RESOURCES, "data"));

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

}