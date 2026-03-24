/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.interlisp.graphics.FontStack;
import org.interlisp.graphics.FontStackDefinitions;
import org.interlisp.unicode.MccsToUnicode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Collectors;

import static java.lang.String.format;

/*
 * Download Web fonts and see how well they cover the MCCS character set.
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 */
public class FontCoverage {

    private static class Args {
        @Parameter(names = {"-o", "--out"})
        private File outputFile;
        @Parameter(names = {"-m", "--missingOnly"}, arity = 0)
        boolean onlyShowMissing;
        @Parameter(names = {"-c"}, arity = 0)
        boolean showMissingCharNames;
    }

    /**
     * If the user requests to see a list of missing characters ("-c"),
     * don't bother with characters below this codepoint.
     */
    private static final int SPACE = 32;

    private static final File RESOURCES = new File("src/main/resources");

    static {
        MccsToUnicode.init(new File(RESOURCES, "data"));
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final MccsToUnicode mccsToUnicode = MccsToUnicode.getInstance();

    public static void main(String[] args) throws IOException, URISyntaxException, FontFormatException {
        final Args programArgs = new Args();
        JCommander.newBuilder().addObject(programArgs).build().parse(args);

        final FontCoverage fontCoverage = new FontCoverage();
        final boolean onlyShowMissing = programArgs.onlyShowMissing;
        final boolean showMissingCharacterNames = programArgs.showMissingCharNames;

        final FontStackDefinitions fsDefs = new FontStackDefinitions();

        try (final PrintWriter writer = (programArgs.outputFile == null ? new PrintWriter(System.out) : new PrintWriter(programArgs.outputFile))) {
            for (FontStack stack : fsDefs.getAllStacks()) {
                fontCoverage.showCoverage(writer, stack, onlyShowMissing, showMissingCharacterNames);
            }
        }
    }

    /**
     * Log the percent of each charset that's displayable using the given {@link FontStack}.
     *
     * @param writer where to write the output
     * @param fontStack the font stack
     * @param showOnlyMissing show only the charsets with less than full coverage
     */
    private void showCoverage(PrintWriter writer, FontStack fontStack, boolean showOnlyMissing,
                              boolean showMissingCharacterNames) {

        if (showMissingCharacterNames) {
            writer.println(format("Glyphs missing from font stack %s:", fontStack.getFamilyName()));
        }
        final Map<Integer, Float> fractionMissingPerCharset = new HashMap<>();
        for (int charset : mccsToUnicode.charsets()) {
            final SortedSet<Integer> mccsCodesInCharset = mccsToUnicode.charsetMembers(charset);
            final float numMccsCodesInCharset = mccsCodesInCharset.size();
            int notDisplayableCount = 0;
            for (int mccsCode : mccsCodesInCharset) {
                int unicode = mccsToUnicode.unicode(mccsCode);
                if (!fontStack.isDisplayableByAny((char)unicode)) {
                    if (unicode >= SPACE) {
                        notDisplayableCount++;
                        if (showMissingCharacterNames) {
                            writer.print(format("0x%04X (#o%06o) %05d: '%c', %s\n",
                                    unicode, unicode, unicode, unicode,
                                    Character.getName(unicode)));
                        }
                    }
                }
            }
            fractionMissingPerCharset.put(charset, notDisplayableCount / numMccsCodesInCharset);
        }

        writer.print("\nThe charsets containing chars that won't display, with percent displayable:\n");
        writer.print(format("Stack '%s' contains %d font(s): %s\n", fontStack.getFamilyName(), fontStack.getStack().size(),
                fontStack.getStack().stream().map(Font::getName).collect(Collectors.joining(", "))));
        fractionMissingPerCharset.forEach((charset, fraction) -> {
            if (!showOnlyMissing || fraction != 0) {
                writer.print(format("0x%02X (#o%03o) (%03d) %s: %d%%\n", charset, charset, charset,
                        mccsToUnicode.charsetName(charset), (int) (100 - fraction * 100)));
            }
        });
        final float totalFractionMissing = fractionMissingPerCharset.values().stream().reduce(0f, Float::sum);
        final float average = totalFractionMissing / fractionMissingPerCharset.size();
        writer.print(format("Percent coverage: %2.1f%%\n\n", (100 - (100 * average))));
    }
}
