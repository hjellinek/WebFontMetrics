/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.graphics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A font "family stack," <a href="https://fonts.google.com/noto/use#use-noto-fonts-as-web-fonts">defined by Google</a> as
 * "multiple font families separated by commas. When the first font does not contain characters necessary to render the
 * text, the browser uses font fallback: it tries the second font, then third and so on. When none of the fonts support
 * a particular character, the browser uses the default system font."
 * <p>
 * We need to define font (family) stacks for each of the font families we deal with to gather metrics for characters
 * beyond XCCS charset 0.
 */
public class FontStack {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private URI baseDownloadUri = URI.create("https://fonts.googleapis.com/css2");

    private final String familyName;

    private final List<String> memberNames;

    protected List<Font> stack;

    /**
     * Create a font stack.
     *
     * @param familyName  the family name of the stack, for informational purposes only
     * @param memberNames the names of the members, which are the names used to download them
     * @throws IOException         if we can't read the fonts
     * @throws URISyntaxException  if the font URI is malformed
     * @throws FontFormatException if the resulting font is unparseable
     */
    public FontStack(String familyName, String... memberNames) throws IOException, URISyntaxException, FontFormatException {
        this.familyName = familyName;
        this.memberNames = Arrays.asList(memberNames);
        load();
    }

    /**
     * Create a font stack with a given base URL (e.g., <tt>https://fonts.googleapis.com/css</tt>).  <b>It must
     * contain no query parameters.</b>
     *
     * @param baseDownloadUri the base URI
     * @param familyName      the family name of the stack, for informational purposes only
     * @param memberNames     the names of the members, which are the names used to download them
     * @throws IOException         if we can't read the fonts
     * @throws URISyntaxException  if the font URI is malformed
     * @throws FontFormatException if the resulting font is unparseable
     */
    public FontStack(URI baseDownloadUri, String familyName, String... memberNames) throws IOException, URISyntaxException, FontFormatException {
        this.familyName = familyName;
        this.memberNames = Arrays.asList(memberNames);
        this.baseDownloadUri = baseDownloadUri;
        load();
    }

    /**
     * Create the complete download URI and return it.
     *
     * @return the download URI
     */
    private URI computeDownloadUri() {
        final String queryString = memberNames.stream().map(name -> URLEncoder.encode(name, StandardCharsets.UTF_8)).
                collect(Collectors.joining("&family=", "?family=", ""));
        return URI.create(baseDownloadUri.toString() + queryString);
    }

    /**
     * Load the fonts in the stack.
     *
     * @throws IOException         if we can't read the fonts
     * @throws URISyntaxException  if the font URI is malformed
     * @throws FontFormatException if the resulting font is unparseable
     */
    private void load() throws IOException, URISyntaxException, FontFormatException {
        final URI downloadUri = computeDownloadUri();
        final WebFontDownloader wfd = new WebFontDownloader(downloadUri.toString());
        stack = wfd.getFonts();
    }

    /**
     * Return the {@link Font}s in the stack.
     *
     * @return the fonts as a list
     */
    public List<Font> getStack() {
        return Collections.unmodifiableList(stack);
    }

    /**
     * Return the names of the member fonts.
     *
     * @return the member font names
     */
    public List<String> getMemberNames() {
        return Collections.unmodifiableList(memberNames);
    }

    /**
     * Return the stack's family name, which is informational.
     *
     * @return the stack's family name
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Return the base of the download URI.
     *
     * @return the download URI base
     */
    public URI getBaseDownloadUri() {
        return baseDownloadUri;
    }

    /**
     * Return the first font in the stack that can display ({@link Font#canDisplay}) the given Unicode character.  If there is none,
     * this returns null.
     *
     * @param ch the character in question
     * @return the first {@link Font} that can display the character, or null if none found
     */
    public Font isDisplayableBy(char ch) {
        return stack.stream().filter(font -> font.canDisplay(ch)).findFirst().orElse(null);
    }

    /**
     * Return true if any font in the stack can display ({@link Font#canDisplay}) the given Unicode character.  Otherwise,
     * return false.
     *
     * @param ch the character in question
     * @return true if any font can display the character
     */
    public boolean isDisplayableByAny(char ch) {
        return stack.stream().anyMatch(font -> font.canDisplay(ch));
    }

    @Override
    public String toString() {
        return "FontStack{" +
                "familyName='" + familyName + '\'' +
                ", memberNames=" + memberNames +
                ", baseDownloadUri=" + baseDownloadUri +
                '}';
    }
}



