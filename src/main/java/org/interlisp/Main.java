package org.interlisp;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.interlisp.graphics.FallbackFontStack;
import org.interlisp.graphics.FontStack;
import org.interlisp.tools.MetricsProcessor;
import org.interlisp.unicode.XccsToUnicode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.util.List;

import static org.interlisp.graphics.FontUtils.f;

/*
 * Download Web fonts and write their metrics to files suitable for use by Medley Interlisp's
 * <tt>HTMLSTREAM</tt>.
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final File RESOURCES = new File("src/main/resources");

    private static final List<Integer> FONT_SIZES = List.of(8, 10, 12, 14, 16, 18, 20, 24, 32, 40, 92);

    /**
     * Scale the fonts by this factor to convert the int measurements to centipoints.
     */
    private static final int FONT_SCALE = 100;

    private static class Args {
        @Parameter(names = {"-d", "--dir"})
        private File dir;
    }

    static {
        XccsToUnicode.init(new File(RESOURCES, "data"));
    }

    public static void main(String[] args) throws IOException, URISyntaxException, FontFormatException {
        final long start = System.currentTimeMillis();

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

        programArgs.dir.mkdirs();
        LOG.info("Will write to {}", programArgs.dir);

        for (FontStack stack : List.of(notoSans, notoSansMono, notoSansDisplay, notoSerif)) {
            new MetricsProcessor(programArgs.dir, stack, FONT_SCALE, FONT_SIZES).writeStackMetrics();
        }

        final long end = System.currentTimeMillis();

        LOG.info("Run finished in {} secs", (end - start) / 1000.0f);
    }

}