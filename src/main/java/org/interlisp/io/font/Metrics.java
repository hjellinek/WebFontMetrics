/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io.font;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.interlisp.io.sexp.LispList.pList;
import static org.interlisp.io.sexp.LispNum.num;
import static org.interlisp.io.sexp.LispString.str;

public class Metrics {

    private final ConvertToLisp cvt = new ConvertToLisp();

    public class FontMetricsEntry {

        private final String family;

        private final int size;

        private final int height;

        private final int style;

        private final int maxAscent;

        private final int maxDescent;

        private final List<CharsetMetricsEntry> charsets = new LinkedList<>();

        public FontMetricsEntry(String family, int size, int height, int style, int maxAscent, int maxDescent) {
            this.family = family;
            this.size = size;
            this.height = height;
            this.style = style;
            this.maxAscent = maxAscent;
            this.maxDescent = maxDescent;
        }

        public String getFamily() {
            return family;
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

        public void addCharset(CharsetMetricsEntry charsetMetrics) {
            charsets.add(charsetMetrics);
        }

        public void addAllCharsets(Collection<CharsetMetricsEntry> metrics) {
            charsets.addAll(metrics);
        }

        public List<CharsetMetricsEntry> getCharsets() {
            return charsets;
        }

        public void write(Writer w) throws IOException {
            pList(":FAMILY", str(family), ":SIZE", num(size),
                    ":HEIGHT", num(height),
                    ":MAX-ASCENT", num(maxAscent), ":MAX-DESCENT", num(maxDescent),
                    ":LISP-NAME", cvt.makeLispFamilyName(family),
                    ":LISP-FACE", cvt.makeLispFaceName(style), ":LISP-SIZE", num(size)
                    ).write(w); // TODO write charsets!
        }

        @Override
        public String toString() {
            return "FontMetricsEntry{" +
                    "family='" + family + '\'' +
                    ", size=" + size +
                    ", height=" + height +
                    ", style=" + style +
                    ", maxAscent=" + maxAscent +
                    ", maxDescent=" + maxDescent +
                    ", charsets=" + charsets.size() +
                    '}';
        }
    }

    private final List<FontMetricsEntry> entries = new LinkedList<>();

    public void add(String family, int size, int height, int style, int maxAscent, int maxDescent) {
        entries.add(new FontMetricsEntry(family, size, height, style, maxAscent, maxDescent));
    }

    public void write(Writer w) throws IOException {
        w.write("(");
        for (FontMetricsEntry e : entries) {
            e.write(w);
            w.write('\n');
        }
        w.write(")\n");
    }

}
