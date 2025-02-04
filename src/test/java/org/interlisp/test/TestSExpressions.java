/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.test;

import org.interlisp.io.LispList;
import org.interlisp.io.Litatom;
import org.interlisp.io.SExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class TestSExpressions {

    private final Writer writer = new BufferedWriter(new OutputStreamWriter(System.out));

    private static final SExpression NIL = null;

    @Test
    void testMakeList() throws IOException {
        LispList l = new LispList();
        for (int i = 0; i < 10; i++) {
            l.add(i);
            l.add(NIL);
        }
        for (int i = 0; i < 5; i++) {
            l.add(new Litatom(":atom"+i));
        }
        final LispList ll = new LispList();
        for (int i = 0; i < 3; i++) {
            ll.add("string "+i);
        }
        l.add(ll);
        for (int i = 0; i < 6; i++) {
            l.add(i*1.0d);
        }

        Assertions.assertEquals(32, l.size());

        l.write(writer);
        writer.write('\n');
        writer.flush();
    }

}
