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
import java.net.URISyntaxException;

/**
 * A {@link FontStack} that will fall back to another one for any characters it's missing.
 */
public class FallbackFontStack extends FontStack {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final FontStack fallback;

    /**
     * Create a {@link FontStack} that uses fonts of its own, but falls back to another {@link FontStack} for fonts
     * it's missing.
     *
     * @param fallback    the {@link FontStack} to fall back on
     * @param familyName  the family name
     * @param memberNames the names of fonts that belong properly to this {@link FontStack}
     * @throws IOException         if we can't read the fonts
     * @throws URISyntaxException  if the font URI is malformed
     * @throws FontFormatException if the resulting font is unparseable
     */
    public FallbackFontStack(FontStack fallback, String familyName, String... memberNames) throws IOException, URISyntaxException, FontFormatException {
        super(familyName, memberNames);
        this.fallback = fallback;
        stack.addAll(fallback.getStack());
    }

    public String toString() {
        return "FallbackFontStack{" +
                "familyName='" + getFamilyName() + '\'' +
                ", memberNames=" + getMemberNames() +
                ", baseDownloadUri=" + getBaseDownloadUri() +
                ", fallback=" + ((fallback == null) ? "null" : fallback.getFamilyName()) +
                '}';
    }
}
