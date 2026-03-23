package org.interlisp;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.interlisp.graphics.FontStack;
import org.interlisp.graphics.FontStackDefinitions;
import org.interlisp.tools.MetricsProcessor;
import org.interlisp.unicode.MccsToUnicode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.util.List;

/*
 * Download Web fonts and write their metrics to files suitable for use by Medley Interlisp's
 * <tt>HTMLSTREAM</tt>.
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final File RESOURCES = new File("src/main/resources");

    /**
     * The font sizes to generate.  Keep this in sync with `HTMLSTREAM`'s constant `*WEB-FONT-SIZES*`.
     */
    private static final List<Integer> FONT_SIZES = List.of(6, 8, 10, 12, 14, 16, 18, 20, 24, 32, 34, 36, 38, 40,
            42, 44, 46, 48, 50, 92);

    /**
     * Scale the fonts by this factor to convert the int measurements to centipoints.
     */
    private static final int FONT_SCALE = 100;

    private static class Args {
        @Parameter(names = {"-d", "--dir"}, required = true)
        private File dir;
    }

    static {
        MccsToUnicode.init(new File(RESOURCES, "data"));
    }

    public static void main(String[] args) throws IOException, URISyntaxException, FontFormatException {

        final Args programArgs = new Args();
        JCommander.newBuilder().addObject(programArgs).build().parse(args);

        final FontStackDefinitions fsDefs = new FontStackDefinitions();

        programArgs.dir.mkdirs();
        LOG.info("Will write to {}", programArgs.dir);

        final long start = System.currentTimeMillis();

        for (FontStack stack : fsDefs.getAllStacks()) {
            new MetricsProcessor(programArgs.dir, stack, FONT_SCALE, FONT_SIZES).writeStackMetrics();
        }

        final long end = System.currentTimeMillis();

        LOG.info("Run finished in {} secs", (end - start) / 1000.0f);
    }

}