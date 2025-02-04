/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.graphics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Download a Web font family by name.
 */
public class WebFontDownloader {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final Pattern URL_EXTRACTOR = Pattern.compile("^ *src: *url\\((.*)\\) *format.*");

    private final String cssUrl;

    /**
     * Download one or more font families from Google or other source.  For example,
     * <tt>https://fonts.googleapis.com/css2?family=Noto+Sans&family=Noto+Serif</tt>.
     * The document that returns is a CSS <tt>@font-face</tt> declaration like
     * <pre>
     * \@font-face {
     *   font-family: 'Noto Sans';
     *   font-style: normal;
     *   font-weight: 400;
     *   font-stretch: normal;
     *   src: url(https://fonts.gstatic.com/s/notosans/v38/o-0mIpQlx3QUlC5A4PNB6Ryti20_6n1iPHjcz6L1SoM-jCpoiyD9A99d.ttf) format('truetype');
     * }
     * </pre>
     * We pull the URLs from each <tt>@font-face</tt> stanza,
     *
     * @param familiesUrl the URL to download from
     */
    public WebFontDownloader(String familiesUrl) {
        cssUrl = familiesUrl;
    }

    private List<String> getUrlsFromCss() throws IOException, URISyntaxException {
        final List<String> fontUrls = new LinkedList<>();

        try (final BufferedReader br = new BufferedReader(new InputStreamReader(new URI(cssUrl).toURL().openStream()))) {
            while (true) {
                final String line = br.readLine();
                if (line == null) {
                    break;
                }
                final Matcher matcher = URL_EXTRACTOR.matcher(line);
                if (matcher.matches()) {
                    fontUrls.add(matcher.group(1));
                }
            }
        }

        return fontUrls;
    }

    /**
     * Return all {@link Font}s found at the URL.
     *
     * @return a list of {@link Font}
     * @throws IOException         if we can't read
     * @throws URISyntaxException  if the URL is bad
     * @throws FontFormatException if the font file is bad
     */
    public List<Font> getFonts() throws IOException, URISyntaxException, FontFormatException {
        final List<String> fontUrls = getUrlsFromCss();
        final List<Font> result = new LinkedList<>();

        for (String fontUrl : fontUrls) {
            try (final BufferedInputStream fontStream = new BufferedInputStream(new URI(fontUrl).toURL().openStream())) {
                final Font[] fonts = Font.createFonts(fontStream);
                result.addAll(Arrays.asList(fonts));
            }
        }

        return result;
    }
}
