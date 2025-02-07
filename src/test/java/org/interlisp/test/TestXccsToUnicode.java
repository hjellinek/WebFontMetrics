/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.test;

import org.interlisp.unicode.XccsToUnicode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestXccsToUnicode {

    private static final File RESOURCES = new File("src/main/resources");

    private static XccsToUnicode XCCS_TO_UNICODE;

    @BeforeAll
    static void beforeAll() throws IOException {
        XCCS_TO_UNICODE = XccsToUnicode.getInstance(new File(RESOURCES, "data"));
    }

    @Test
    void testCharA() {
        assertEquals('A', XCCS_TO_UNICODE.unicode('A'));
    }

    @Test
    void testReplacementChar() {
        assertEquals(0xFFFD, XCCS_TO_UNICODE.unicode(0xF0C7));
    }

    @Test
    void testUnsupportedCharset() {
        assertFalse(XCCS_TO_UNICODE.supportsCharset(0xFF));
    }

    @Test
    void testSupportedCharset() {
        assertTrue(XCCS_TO_UNICODE.supportsCharset(0x00));
    }

    @Test
    void checkExpectedNumCharsets() {
        assertEquals(105, XCCS_TO_UNICODE.numCharsets());
    }

    @Test
    void checkExpectedNumXccsChars() {
        assertEquals(10534, XCCS_TO_UNICODE.numCharacters());
    }

}

