/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io.font;

public class CharsetMetricsEntry {

    private final int charset;

    private final int height;

    private final int maxAscent;

    private final int maxDescent;

    private final int[] widths;

    public CharsetMetricsEntry(int charset, int height, int maxAscent, int maxDescent, int[] widths) {
        this.charset = charset;
        this.height = height;
        this.maxAscent = maxAscent;
        this.maxDescent = maxDescent;
        this.widths = widths;
    }

    public int getCharset() {
        return charset;
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

    public int[] getWidths() {
        return widths;
    }

    @Override
    public String toString() {
        return "CharsetMetricsEntry{" +
                "charset=" + charset +
                ", height=" + height +
                ", maxAscent=" + maxAscent +
                ", maxDescent=" + maxDescent +
                ", widths=" + widths.length +
                '}';
    }
}
