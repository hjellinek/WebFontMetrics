/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.interlisp.graphics.FallbackFontStack;
import org.interlisp.graphics.FontStack;
import org.interlisp.unicode.XccsToUnicode;
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
import static org.interlisp.graphics.FontUtils.f;

/*
 * Download Web fonts and see how well they cover the XCCS character set.
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 */
public class FontCoverage {

    private static class Args {
        @Parameter(names = {"-o", "--out"})
        private File outputFile;
        @Parameter(names = {"-m", "--missingOnly"}, arity = 0)
        boolean onlyShowMissing;
    }

    private static final File RESOURCES = new File("src/main/resources");

    static {
        XccsToUnicode.init(new File(RESOURCES, "data"));
    }

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final XccsToUnicode xccsToUnicode = XccsToUnicode.getInstance();

    public static void main(String[] args) throws IOException, URISyntaxException, FontFormatException {
        final Args programArgs = new Args();
        JCommander.newBuilder().addObject(programArgs).build().parse(args);

        final FontStack notoSans = new FontStack("Noto Sans", "Noto Sans",
                "Noto Sans Simplified Chinese", "Noto Sans Traditional Chinese", "Noto Sans JP", "Noto Sans KR",
                "Noto Sans Arabic", "Noto Sans Hebrew", "Noto Sans Runic",
                "Noto Sans Georgian", "Noto Sans Armenian", "Noto Sans Thai", "Noto Sans Lao",
                "Noto Sans Gurmukhi", "Noto Sans Bengali",
                "Noto Sans Math", "Noto Sans Symbols", "Noto Sans Symbols 2");
        final FontStack notoSansMono = new FallbackFontStack(notoSans, "Noto Sans Mono", "Noto Sans Mono");
        final FontStack notoSansDisplay = new FallbackFontStack(notoSans, "Noto Sans Display", "Noto Sans Display");
        final FontStack notoSerif = new FontStack("Noto Serif", "Noto Serif",
                "Noto Serif Simplified Chinese", "Noto Serif Traditional Chinese", "Noto Serif JP", "Noto Serif KR",
                "Noto Naskh Arabic", "Noto Serif Hebrew", f("Noto Sans Runic"),
                "Noto Serif Georgian", "Noto Serif Armenian", "Noto Serif Thai", "Noto Serif Lao",
                "Noto Serif Devanagari",
                "Noto Serif Gurmukhi", "Noto Serif Bengali",
                f("Noto Sans Math"), f("Noto Sans Symbols"), f("Noto Sans Symbols 2"));


        final FontCoverage fontCoverage = new FontCoverage();
        final boolean onlyShowMissing = programArgs.onlyShowMissing;

        try (final PrintWriter writer = (programArgs.outputFile == null ? new PrintWriter(System.out) : new PrintWriter(programArgs.outputFile))) {
            fontCoverage.showCoverage(writer, notoSans, onlyShowMissing);
            fontCoverage.showCoverage(writer, notoSansMono, onlyShowMissing);
            fontCoverage.showCoverage(writer, notoSansDisplay, onlyShowMissing);
            fontCoverage.showCoverage(writer, notoSerif, onlyShowMissing);
        }
    }

    /**
     * Log the percent of each charset that's displayable using the given {@link FontStack}.
     *
     * @param writer where to write the output
     * @param fontStack the font stack
     * @param showOnlyMissing show only the charsets with less than full coverage
     */
    private void showCoverage(PrintWriter writer, FontStack fontStack, boolean showOnlyMissing) {
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

        writer.print("The charsets containing chars that won't display, with percent displayable:\n");
        writer.print(format("Stack '%s' contains %d font(s): %s\n", fontStack.getFamilyName(), fontStack.getStack().size(),
                fontStack.getStack().stream().map(Font::getName).collect(Collectors.joining(", "))));
        fractionMissingPerCharset.forEach((charset, fraction) -> {
            if (!showOnlyMissing || fraction != 0) {
                writer.print(format("0x%s (#o%s) (%s) %s: %d%%\n",
                        Integer.toHexString(charset).toUpperCase(), Integer.toOctalString(charset), charset,
                        xccsToUnicode.charsetName(charset), (int) (100 - fraction * 100)));
            }
        });
        final float totalFractionMissing = fractionMissingPerCharset.values().stream().reduce(0f, Float::sum);
        final float average = totalFractionMissing / fractionMissingPerCharset.size();
        writer.print(format("Percent coverage: %2.1f%%\n\n", (100 - (100 * average))));
    }
}
