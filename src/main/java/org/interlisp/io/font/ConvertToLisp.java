/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io.font;

import org.interlisp.io.sexp.LispList;
import org.interlisp.io.sexp.Litatom;

import java.awt.*;

import static org.interlisp.io.sexp.Litatom.atom;

public class ConvertToLisp {

    /**
     * Convert a font family name to a {@link String} we can turn into a {@link Litatom}.
     * A family name like "Noto Sans Display" becomes "NOTO-SANS-DISPLAY".
     *
     * @param family the font family name, mixed case with spaces
     * @return an Interlisp-friendly name as a {@link Litatom}
     */
    public String makeLispFamilyNameStr(String family) {
        final String upperCaseVersion = family.toUpperCase();
        final StringBuilder sb = new StringBuilder();
        upperCaseVersion.chars().forEach(c -> {
            if (c == ' ') {
                sb.append('-');
            } else {
                sb.append((char) c);
            }
        });
        return sb.toString();
    }

    /**
     * Convert a font family name to a {@link Litatom}.  A family name like "Noto Sans Display" becomes
     * "NOTO-SANS-DISPLAY".
     *
     * @param family the font family name, mixed case with spaces
     * @return an Interlisp-friendly name as a {@link Litatom}
     */
    public Litatom makeLispFamilyName(String family) {
        return atom(makeLispFamilyNameStr(family));
    }


    private static final Litatom REGULAR = new Litatom("REGULAR");

    /**
     * Convert a font's style and weight to an Interlisp font-face triple.
     * We only support bold and medium weights and italic and regular styles.
     *
     * @param style normal, italic, bold, etc., see {@link Font#getStyle()}
     * @return a list of [weight slope expansion], e.g., <tt>(BOLD ITALIC REGULAR)</tt>
     */
    public LispList makeLispFaceTriple(int style) {
        final Litatom weight = atom((style & Font.BOLD) != 0 ? "BOLD" : "MEDIUM");
        final Litatom slope = atom((style & Font.ITALIC) != 0 ? "ITALIC" : "REGULAR");
        return new LispList(weight, slope, REGULAR);
    }

}
