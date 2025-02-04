/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class LispList implements SExpression {

    private final List<SExpression> list;

    public LispList() {
        list = new LinkedList<>();
    }

    @Override
    public void write(Writer w) throws IOException {
        w.write("(");
        for (SExpression sx : list) {
            sx.write(w);
            w.write(" ");
        }
        w.write(")");
    }

    public void add(SExpression s) {
        list.add(Objects.requireNonNullElseGet(s, LispNil::new));
    }

    public void add(String s) {
        list.add(new LispString(s));
    }

    public void add(Number n) {
        list.add(new LispNumber(n));
    }

    public void addAll(Collection<SExpression> all) {
        list.addAll(all);
    }

    public int size() {
        return list.size();
    }

    @Override
    public String toString() {
        return "LispList{" +
                "list=" + list +
                '}';
    }
}
