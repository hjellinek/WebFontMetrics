/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.test;

import org.interlisp.unicode.MccsToUnicode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestMccsToUnicode {

    private static final File RESOURCES = new File("src/main/resources");

    static {
        MccsToUnicode.init(new File(RESOURCES, "data"));
    }

    private static MccsToUnicode MCCS_TO_UNICODE;

    @BeforeAll
    static void beforeAll() throws IOException {
        MCCS_TO_UNICODE = MccsToUnicode.getInstance();
    }

    @Test
    void testCharA() {
        assertEquals('A', MCCS_TO_UNICODE.unicode('A'));
    }

    @Test
    void testReplacementChar() {
        assertEquals(0xFFFD, MCCS_TO_UNICODE.unicode(0xF0C7));
    }

    @Test
    void testUnsupportedCharset() {
        assertFalse(MCCS_TO_UNICODE.supportsCharset(0xFF));
    }

    @Test
    void testSupportedCharset() {
        assertTrue(MCCS_TO_UNICODE.supportsCharset(0x00));
    }

    @Test
    void checkExpectedNumCharsets() {
        assertEquals(105, MCCS_TO_UNICODE.numCharsets());
    }

    @Test
    void checkExpectedNumXccsChars() {
        assertEquals(10535, MCCS_TO_UNICODE.numCharacters());
    }

}

