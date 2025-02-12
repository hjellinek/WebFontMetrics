/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io.font;

import org.interlisp.io.sexp.CanWriteSExp;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.interlisp.io.sexp.LispList.list;
import static org.interlisp.io.sexp.LispList.pList;
import static org.interlisp.io.sexp.LispNum.num;

/**
 * Write a standalone Web font metrics file for <tt>\\HTML.READ-FONT-METRICS</tt> to read.
 */
public class WebFontDescr implements CanWriteSExp {

    private final ConvertToLisp cvt = new ConvertToLisp();

    private final String name;

    private final int style;

    private final int size;

    private final int height;

    private final int maxAscent;

    private final int maxDescent;

    private final int slugWidth;

    private final SortedSet<Integer> charsets = new TreeSet<>();

    public WebFontDescr(String name, int size, int height, int style,
                        int maxAscent, int maxDescent, int slugWidth, Collection<Integer> charsets) {
        this.name = name;
        this.size = size;
        this.height = height;
        this.style = style;
        this.maxAscent = maxAscent;
        this.maxDescent = maxDescent;
        this.slugWidth = slugWidth;
        this.charsets.addAll(charsets);
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getHeight() {
        return height;
    }

    public int getStyle() {
        return style;
    }

    public int getMaxAscent() {
        return maxAscent;
    }

    public int getMaxDescent() {
        return maxDescent;
    }

    public int getSlugWidth() {
        return slugWidth;
    }

    public SortedSet<Integer> getCharsets() {
        return charsets;
    }

    public void write(Writer w) throws IOException {
        pList(":NAME", cvt.makeLispFamilyName(name), ":FACE", cvt.makeLispFaceTriple(style),
                ":SIZE", num(size), ":HEIGHT", num(height),
                ":MAX-ASCENT", num(maxAscent), ":MAX-DESCENT", num(maxDescent),
                ":SLUG-WIDTH", num(slugWidth),
                ":CHARSETS", list(charsets)).write(w);
        w.write('\n');
    }

    @Override
    public String toString() {
        return "WebFontDescr{" +
                "name='" + name + '\'' +
                ", style=" + style +
                ", size=" + size +
                ", height=" + height +
                ", maxAscent=" + maxAscent +
                ", maxDescent=" + maxDescent +
                ", slugWidth=" + slugWidth +
                ", charsets=" + charsets.size() +
                '}';
    }
}
