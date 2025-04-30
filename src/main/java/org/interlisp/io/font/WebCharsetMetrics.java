/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io.font;

import org.interlisp.io.sexp.CanWriteSExp;

import java.io.IOException;
import java.io.Writer;

import static org.interlisp.io.sexp.LispList.list;
import static org.interlisp.io.sexp.LispList.pList;
import static org.interlisp.io.sexp.LispNum.num;

public record WebCharsetMetrics(int charset, int maxAscent, int maxDescent,
                                int maxHeight, int[] widths) implements CanWriteSExp {

    public void write(Writer w) throws IOException {
        pList(":CHARSET", num(charset),
                ":MAX-ASCENT", num(maxAscent), ":MAX-DESCENT", num(maxDescent),
                ":XCCS-WIDTHS", list(widths)
        ).write(w);
    }

    @Override
    public String toString() {
        return "CharsetMetricsEntry{" +
                "charset=0x" + Integer.toHexString(charset).toUpperCase() +
                ", maxAscent=" + maxAscent +
                ", maxDescent=" + maxDescent +
                ", maxHeight=" + maxHeight +
                ", widths=" + widths.length +
                '}';
    }
}
