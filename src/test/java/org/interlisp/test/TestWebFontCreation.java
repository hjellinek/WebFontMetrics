/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.test;

import org.interlisp.graphics.WebFontDownloader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class TestWebFontCreation {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * A one-font CSS URL.
     */
    private static final String ONE_FAMILY_CSS_URL = "https://fonts.googleapis.com/css2?family=Noto+Sans";

    /**
     * A multi-font CSS URL.
     */
    private static final String TWO_FAMILY_CSS_URL = "https://fonts.googleapis.com/css2?family=Noto+Sans&family=Noto+Serif";

    /**
     * A multi-font CSS URL.
     */
    private static final String FOUR_FAMILY_CSS_URL = "https://fonts.googleapis.com/css2?family=Noto+Sans&"+
            "family=Noto+Sans+Display&family=Noto+Sans+Mono&family=Noto+Serif";

    /**
     * Taken from the payload of {@link #ONE_FAMILY_CSS_URL}.
     */
    private static final String NOTO_SANS_URL = "https://fonts.gstatic.com/s/notosans/v38/o-0mIpQlx3QUlC5A4PNB6Ryti20_6n1iPHjcz6L1SoM-jCpoiyD9A99d.ttf";

    @Test
    void testDownloadingRawFont() throws IOException, FontFormatException, URISyntaxException {
        try (final BufferedInputStream fontStream = new BufferedInputStream(new URI(NOTO_SANS_URL).toURL().openStream())) {
            final Font[] fonts = Font.createFonts(fontStream);
            Assertions.assertNotNull(fonts);
            Assertions.assertNotEquals(0, fonts.length);
        }
    }

    void testDownloading(String uri, int numExpected) throws IOException, URISyntaxException, FontFormatException {
        final WebFontDownloader wfd = new WebFontDownloader(uri);
        final List<Font> fonts = wfd.getFonts();
        Assertions.assertNotNull(fonts);
        Assertions.assertEquals(numExpected, fonts.size());
//      fonts.forEach(f -> log.info("{}", FontUtils.describe(f)));
    }

    @Test
    void testDownloadingFromOneFontCss() throws IOException, URISyntaxException, FontFormatException {
        testDownloading(ONE_FAMILY_CSS_URL, 1);
    }

    @Test
    void testDownloadingFromTwoFontCss() throws IOException, URISyntaxException, FontFormatException {
        testDownloading(TWO_FAMILY_CSS_URL, 2);
    }

    @Test
    void testDownloadingFromFourFontCss() throws IOException, URISyntaxException, FontFormatException {
        testDownloading(FOUR_FAMILY_CSS_URL, 4);
    }
}
