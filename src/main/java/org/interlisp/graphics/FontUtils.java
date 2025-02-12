/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.graphics;

import java.awt.*;

public class FontUtils {

    /**
     * You can't instantiate one of these.
     */
    private FontUtils() {
    }

    /**
     * Convert a number of points to CSS pixels. 1 px = 1/96th of 1 in, 1 pt = 1/72nd of 1 in.
     * So pixels = 1.33333 * points.
     *
     * @param points a number of points
     * @return {number} the equivalent number of points
     */
    public static float pointsToCssPixels(float points) {
        return points * 1.33333f;
    }

    /**
     * A method that simply marks a font name as being a substitute for the correct one, as when
     * we use a sans serif font in place of a serif one that doesn't exist yet.  For instance, <tt>f("Noto Sans Math")</tt>
     * in a list of "Noto Serif" font names.
     *
     * @param substituting the name we're marking
     * @return the name we marked
     */
    public static String f(String substituting) {
        return substituting;
    }

    /**
     * Return Medley's name for the weight of the provided style.
     *
     * @param style the {@link Font#getStyle()}
     * @return the name
     */
    public static String weight(int style) {
        if ((style & Font.BOLD) != 0) {
            return "BOLD";
        } else {
            return "MEDIUM";
        }
    }

    /**
     * Return Medley's name for the slope of the provided style.
     *
     * @param style the {@link Font#getStyle()}
     * @return the name
     */
    public static String slope(int style) {
        if ((style & Font.ITALIC) != 0) {
            return "ITALIC";
        } else {
            return "REGULAR";
        }
    }

    public static String describe(Font f) {
        final String strStyle;

        if (f.isBold()) {
            strStyle = f.isItalic() ? "bold italic" : "bold";
        } else {
            strStyle = f.isItalic() ? "italic" : "plain";
        }

        return "family=" + f.getFamily() + ", name=" + f.getName() + ", style=" +
                strStyle + ", size=" + f.getSize();
    }
}

