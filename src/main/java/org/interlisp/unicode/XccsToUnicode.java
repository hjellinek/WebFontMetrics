/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.unicode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Map XCCS 2.0.0 to Unicode.
 */
public class XccsToUnicode {

    private final Logger log = LoggerFactory.getLogger(XccsToUnicode.class);

    private static final String DATA_FILE_NAME = "xccs_to_unicode.txt";

    private static final Pattern PAIR = Pattern.compile("0x(\\p{XDigit}\\p{XDigit}\\p{XDigit}\\p{XDigit}) *0x(\\p{XDigit}\\p{XDigit}\\p{XDigit}\\p{XDigit})");

    private final boolean debug;

    private final Map<Integer, Integer> xccsToUnicode = new HashMap<>(12000);

    private final Map<Integer, SortedSet<Integer>> xccsCharsetToCodes = new HashMap<>(256);

    /**
     * Load the data into xccsToUnicode and xccsCharsetToCodes.
     *
     * @param fromDir the directory holding the file
     * @param debug   if true, dump comments to the log
     */
    public XccsToUnicode(File fromDir, boolean debug) throws IOException {
        this.debug = debug;
        loadData(fromDir);
    }

    /**
     * Load the data into xccsToUnicode and xccsCharsetToCodes.  Don't dump comments to the log.
     *
     * @param fromDir the directory holding the file
     */
    public XccsToUnicode(File fromDir) throws IOException {
        this(fromDir, false);
    }

    /**
     * Return the charset portion of the XCCS code.
     *
     * @param xccsCode the XCCS code
     * @return the charset
     */
    public static int charset(int xccsCode) {
        return (xccsCode >> 8) & 0xFF;
    }

    /**
     * Return the character code portion of the XCCS code.
     *
     * @param xccsCode the XCCS code
     * @return the character code
     */
    public static int charCode(int xccsCode) {
        return xccsCode & 0xFF;
    }

    private void loadData(File fromDir) throws IOException {
        try (final BufferedReader in = new BufferedReader(new FileReader(new File(fromDir, DATA_FILE_NAME)))) {
            while (true) {
                final String line = in.readLine();
                if (line == null) {
                    break;
                }
                if (!line.startsWith("//")) {
                    final Matcher matcher = PAIR.matcher(line);
                    if (matcher.matches()) {
                        final int xccsValue = Integer.parseInt(matcher.group(1), 16);
                        final int unicodeValue = Integer.parseInt(matcher.group(2), 16);
                        xccsToUnicode.put(xccsValue, unicodeValue);
                        xccsCharsetToCodes.computeIfAbsent(charset(xccsValue), key -> new TreeSet<>()).add(xccsValue);
                    }
                } else if (debug) {
                    log.warn("Comment: {}", line);
                }
            }
        }
    }

    /**
     * Return the Unicode value for the given XCCS code.  The result may be null.
     *
     * @return the corresponding Unicode character
     */
    public Integer unicode(int xccs) {
        return xccsToUnicode.get(xccs);
    }

    /**
     * For a given XCCS charset, return the XCCS characters that belong to it (that map to Unicode).
     *
     * @param xccsCharset the XCCS charset
     * @return an unmodifiable set of the constituent characters, sorted
     */
    public SortedSet<Integer> charsetMembers(int xccsCharset) {
        return Collections.unmodifiableSortedSet(xccsCharsetToCodes.get(xccsCharset));
    }

    /**
     * Return all charsets, sorted.
     *
     * @return all charsets
     */
    public SortedSet<Integer> charsets() {
        return Collections.unmodifiableSortedSet(new TreeSet<>(xccsCharsetToCodes.keySet()));
    }

    /**
     * Return true if we support the given XCCS charset.
     *
     * @param charset the charset to check
     * @return true if we support it
     */
    public boolean supportsCharset(int charset) {
        return xccsCharsetToCodes.containsKey(charset);
    }

    /**
     * Return the number of character sets we support.
     *
     * @return the number of character sets we support
     */
    public int numCharsets() {
        return xccsCharsetToCodes.size();
    }

    /**
     * Return the number of XCCS characters we've mapped to Unicode.
     *
     * @return the number of XCCS characters we've mapped to Unicode
     */
    public int numCharacters() {
        return xccsToUnicode.size();
    }
}

