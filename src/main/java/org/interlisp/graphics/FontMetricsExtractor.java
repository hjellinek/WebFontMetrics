/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.graphics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Extract and return font metrics from font files, directories, or collections of {@link Font}
 * objects.
 */
public class FontMetricsExtractor {

    private final Logger log = LoggerFactory.getLogger(FontMetricsExtractor.class);

    private final Graphics2D graphics;

    /**
     * Create an object that can extract and return font metrics from font files, directories, or collections of {@link Font}
     * objects.
     */
    public FontMetricsExtractor() {
        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphics = graphicsEnvironment.createGraphics(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        // listAvailableFontFamilyNames(graphicsEnvironment);
    }

    /**
     * Ask Java for the font families installed in the given {@link GraphicsEnvironment}.
     *
     * @param graphicsEnvironment the environment we're interested in
     */
    private void listAvailableFontFamilyNames(GraphicsEnvironment graphicsEnvironment) {
        final String[] availableFontFamilyNames = graphicsEnvironment.getAvailableFontFamilyNames();
        for (String name : availableFontFamilyNames) {
            log.info("Font: '{}'", name);
        }
    }

    /**
     * Extract {@link FontMetrics} from the supplied font file, which may contain multiple fonts.
     *
     * @param fontFile  the font file
     * @param pointSize the desired font size, in points
     * @return the FontMetrics for each of the constituent fonts
     * @throws IOException if we can't access the file
     */
    public Collection<FontMetrics> fromFontFile(File fontFile, float pointSize) throws IOException, FontFormatException {
        final Font[] newFonts = Font.createFonts(fontFile);
        final java.util.List<FontMetrics> result = new LinkedList<>();
        for (Font newFont : newFonts) {
//            graphicsEnvironment.registerFont(newFont);
            final FontMetrics fontMetrics = graphics.getFontMetrics(newFont.deriveFont(pointSize));
            result.add(fontMetrics);
        }
        return result;
    }

    /**
     * Enumerate and gather metrics from all font files in the given directory.
     *
     * @param dir       the directory
     * @param pointSize the size of the font, in points
     * @param type      the file type (extension), e.g., "ttf"
     * @return the {@link FontMetrics} for all the font files found
     * @throws IOException if we can't read the directory
     */
    public Collection<FontMetrics> fromFontDirectory(File dir, float pointSize, String type) throws IOException {
        try (final DirectoryStream<Path> fileList = Files.newDirectoryStream(dir.toPath())) {
            final java.util.List<FontMetrics> result = new LinkedList<>();
            for (Path filePath : fileList) {
                if (!Files.isDirectory(filePath) && filePath.toString().endsWith(type)) {
                    try {
                        result.addAll(fromFontFile(filePath.toFile(), pointSize));
                    } catch (IOException | FontFormatException e) {
                        log.error("", e);
                    }
                }
            }
            return result;
        }
    }

    /**
     * Enumerate and gather metrics from all TrueType font files in the given directory.
     *
     * @param dir the directory
     * @return the {@link FontMetrics} for all the font files found
     * @throws IOException if we can't read the directory
     */
    public Collection<FontMetrics> fromFontDirectory(File dir, float pointSize) throws IOException {
        return fromFontDirectory(dir, pointSize, "ttf");
    }

    /**
     * Fetch the metrics from a collection of fonts.
     *
     * @param fonts the fonts
     * @return the metrics collection
     */
    public Collection<FontMetrics> fromFonts(Collection<Font> fonts) {
        return fonts.stream().map(graphics::getFontMetrics).toList();
    }

    /**
     * Fetch the metrics for a given {@link Font}.
     *
     * @param font the font
     * @return the {@link FontMetrics}
     */
    public FontMetrics fromFont(Font font) {
        return graphics.getFontMetrics(font);
    }

    public static class FontMeasurements {
        private int height;
        private int maxAscent;
        private int maxDescent;

        public void setValues(int height, int maxAscent, int maxDescent) {
            this.height = height;
            this.maxAscent = maxAscent;
            this.maxDescent = maxDescent;
        }

        public int getHeight() {
            return height;
        }

        public int getMaxAscent() {
            return maxAscent;
        }

        public int getMaxDescent() {
            return maxDescent;
        }
    }
}
