/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.graphics;

import org.interlisp.io.font.WebCharsetMetrics;
import org.interlisp.unicode.XccsToUnicode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.interlisp.unicode.XccsToUnicode.REPLACEMENT_CHAR;

/**
 * A font "family stack," <a href="https://fonts.google.com/noto/use#use-noto-fonts-as-web-fonts">defined by Google</a> as
 * "multiple font families separated by commas. When the first font does not contain characters necessary to render the
 * text, the browser uses font fallback: it tries the second font, then third and so on. When none of the fonts support
 * a particular character, the browser uses the default system font."
 * <p>
 * We need to define font (family) stacks for each of the font families we deal with to gather metrics for characters
 * beyond XCCS charset 0.
 */
@SuppressWarnings("JavadocLinkAsPlainText")
public class FontStack {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * For some reason, our character widths are 75% of what Interlisp (SVG) expects them to be.
     */
    private static final float WHY_DO_WE_HAVE_TO_SCALE = 1.33333f;

    private URI baseDownloadUri = URI.create("https://fonts.googleapis.com/css2");

    private final XccsToUnicode xccsToUnicode = XccsToUnicode.getInstance();

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
     * Return the first font in the collection of derived fonts that can display ({@link Font#canDisplay}) the given Unicode character.
     * If there is none, this returns null.
     *
     * @param derived the derived {@link Font}s
     * @param ch      the character in question
     * @return the first {@link Font} that can display the character, or null if none found
     */
    public Font isDisplayableBy(Collection<Font> derived, char ch) {
        return derived.stream().filter(font -> font.canDisplay(ch)).findFirst().orElse(null);
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

    /**
     * Gather the metrics from the stack for all XCCS charsets.
     *
     * @param size                 font size in points
     * @param style                font style, see {@link Font#getStyle()}
     * @param returnedMeasurements an instance of {@link org.interlisp.graphics.FontMetricsExtractor.FontMeasurements} <b>that
     *                             will be updated with the line measurements of the constituent fonts</b>.
     * @return a {@link WebCharsetMetrics} containing the metrics
     */
    public List<WebCharsetMetrics> getAllCharsetMetrics(int size, int style,
                                                              FontMetricsExtractor.FontMeasurements returnedMeasurements) {
        final FontMetricsExtractor fme = new FontMetricsExtractor();
        //noinspection MagicConstant
        final Collection<Font> derivedFonts = stack.stream().map(font -> font.deriveFont(style, size)).toList();
        int maxSlugWidth = fme.fromFonts(derivedFonts).stream().mapToInt(fm -> fm.charWidth(REPLACEMENT_CHAR)).max().
                orElseThrow(() -> new IllegalStateException("Fonts do not contain REPLACEMENT_CHAR"));

        final List<WebCharsetMetrics> result = new LinkedList<>();

        int fontMaxAscent = 0;
        int fontMaxDescent = 0;
        int fontMaxHeight = 0;

        // for each XCCS charset, find the font that can display (measure) it and get its width
        for (Integer xccsCharset : xccsToUnicode.charsets()) {
            int widthIndex = 0;
            final int[] charsetWidths = new int[256];

            int charsetMaxAscent = 0;
            int charsetMaxDescent = 0;
            int charsetMaxHeight = 0;

            for (Integer xccsChar : xccsToUnicode.charsetMembers(xccsCharset)) {
                int unicode = xccsToUnicode.unicode(xccsChar);
                // loop over the derived fonts until we find one that can measure the character
                final Font canDisplayIt = isDisplayableBy(derivedFonts, (char)unicode);
                if (canDisplayIt != null) {
                    final FontMetrics metricsForThatFont = fme.fromFont(canDisplayIt);
                    charsetMaxAscent = Math.max(charsetMaxAscent, metricsForThatFont.getAscent());
                    charsetMaxDescent = Math.max(charsetMaxDescent, metricsForThatFont.getDescent());
                    charsetMaxHeight = Math.max(charsetMaxHeight, metricsForThatFont.getHeight());
                    final int width = metricsForThatFont.charWidth(unicode);
                    charsetWidths[widthIndex] = (int)(WHY_DO_WE_HAVE_TO_SCALE * width);
                }
                widthIndex++;
            }

            fontMaxHeight = Math.max(fontMaxHeight, charsetMaxHeight);
            fontMaxAscent = Math.max(fontMaxAscent, charsetMaxAscent);
            fontMaxDescent = Math.max(fontMaxDescent, charsetMaxDescent);

            final WebCharsetMetrics charsetMetrics =
                    new WebCharsetMetrics(xccsCharset, charsetMaxAscent, charsetMaxDescent,
                            charsetMaxHeight, charsetWidths);
            result.add(charsetMetrics);
        }

        // return these values by updating returnedMeasurements.  I'm sorry.
        returnedMeasurements.setValues(fontMaxHeight, fontMaxAscent, fontMaxDescent, maxSlugWidth);

        return result;
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



