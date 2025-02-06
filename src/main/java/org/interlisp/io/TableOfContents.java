/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import static org.interlisp.io.LispList.pList;
import static org.interlisp.io.LispNum.num;

/**
 * The table of contents of the metrics data file.
 */
public class TableOfContents {

    private final ConvertToLisp cvt = new ConvertToLisp();

    private final List<Entry> entries = new LinkedList<>();

    public class Entry {
        private String name;
        private int style;
        private int size;

        public Entry(String name, int size, int style) {
            this.name = name;
            this.size = size;
            this.style = style;
        }

        public void write(Writer w) throws IOException {
            pList(":FAMILY", cvt.makeLispFamilyName(name), ":SIZE", num(size),
                    ":FACE", cvt.makeLispFaceName(style)).write(w);
        }
    }

    public TableOfContents() {
    }

    public void add(String name, int size, int style) {
        entries.add(new Entry(name, size, style));
    }

    public void write(Writer w) throws IOException {
        w.write("(");
        for (Entry e : entries) {
            e.write(w);
            w.write('\n');
        }
        w.write(")\n");
    }
}
