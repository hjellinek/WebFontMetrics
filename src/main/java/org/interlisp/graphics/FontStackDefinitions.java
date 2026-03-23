/*
 *
 * Copyright 2026 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.graphics;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.interlisp.graphics.FontUtils.f;

/**
 * Central definitions for the {@link FontStack}s we're using.
 */
@SuppressWarnings("unused")
public class FontStackDefinitions {

    final FontStack notoSans = new FontStack("Noto Sans", "Noto Sans",
            "Noto Sans SC", "Noto Sans TC", "Noto Sans JP", "Noto Sans KR",
            "Noto Sans Arabic", "Noto Sans Hebrew", "Noto Sans Runic",
            "Noto Sans Georgian", "Noto Sans Armenian", "Noto Sans Thai", "Noto Sans Lao",
            "Noto Sans Gurmukhi", "Noto Sans Bengali",
            "Noto Sans Math", "Noto Sans Symbols", "Noto Sans Symbols 2");
    final FontStack notoSansMono = new FallbackFontStack(notoSans, "Noto Sans Mono", "Noto Sans Mono");
    final FontStack notoSansDisplay = new FallbackFontStack(notoSans, "Noto Sans Display", "Noto Sans Display");
    final FontStack notoSerif = new FontStack("Noto Serif", "Noto Serif",
            "Noto Serif SC", // missing: "Noto Serif Traditional Chinese",
            "Noto Serif JP", "Noto Serif KR",
            "Noto Naskh Arabic", "Noto Serif Hebrew", f("Noto Sans Runic"),
            "Noto Serif Georgian", "Noto Serif Armenian", "Noto Serif Thai", "Noto Serif Lao",
            "Noto Serif Devanagari",
            "Noto Serif Gurmukhi", "Noto Serif Bengali",
            f("Noto Sans Math"), f("Noto Sans Symbols"), f("Noto Sans Symbols 2"));
    final FontStack notoSerifDisplay = new FallbackFontStack(notoSerif, "Noto Serif Display", "Noto Serif Display");

    public FontStackDefinitions() throws IOException, URISyntaxException, FontFormatException {
    }

    public FontStack getNotoSans() {
        return notoSans;
    }

    public FontStack getNotoSansMono() {
        return notoSansMono;
    }

    public FontStack getNotoSansDisplay() {
        return notoSansDisplay;
    }

    public FontStack getNotoSerif() {
        return notoSerif;
    }

    public FontStack getNotoSerifDisplay() {
        return notoSerifDisplay;
    }

    public List<FontStack> getAllStacks() {
        return List.of(notoSans, notoSansMono, notoSansDisplay, notoSerif, notoSerifDisplay);
    }
}
