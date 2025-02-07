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

    private static final Logger LOG = LoggerFactory.getLogger(XccsToUnicode.class);

    private static final String XCCS_TO_UNICODE_DATA_FILE = "xccs_to_unicode.txt";

    private static final String XCCS_CHARSET_NAMES_DATA_FILE = "xccs_charset_names.txt";

    private static final Pattern CHAR_MAPPING_PAIR =
            Pattern.compile("0x(\\p{XDigit}\\p{XDigit}\\p{XDigit}\\p{XDigit}) *0x(\\p{XDigit}\\p{XDigit}\\p{XDigit}\\p{XDigit})");

    private static final Pattern CHARSET_NAME_EXTRACTOR =
            Pattern.compile("\\d+ +(\\d+) +(.*) *$");

    private final boolean debug;

    private final Map<Integer, Integer> xccsToUnicode = new HashMap<>(12000);

    private final Map<Integer, String> xccsCharsetToName = new HashMap<>(120);

    private final Map<Integer, SortedSet<Integer>> xccsCharsetToCodes = new HashMap<>(256);

    private static XccsToUnicode SINGLETON = null;

    public synchronized static void init(File fromDir) {
        if (SINGLETON == null) {
            try {
                SINGLETON = new XccsToUnicode(fromDir);
            } catch (IOException e) {
                LOG.error("", e);
            }
        }
    }

    public static XccsToUnicode getInstance() {
        return SINGLETON;
    }

    /**
     * Load the data into xccsToUnicode and xccsCharsetToCodes.
     *
     * @param fromDir the directory holding the file
     * @param debug   if true, dump comments to the log
     */
    public XccsToUnicode(File fromDir, boolean debug) throws IOException {
        this.debug = debug;
        loadMappingData(fromDir);
        loadCharsetNameData(fromDir);
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

    private void loadMappingData(File fromDir) throws IOException {
        try (final BufferedReader in = new BufferedReader(new FileReader(new File(fromDir, XCCS_TO_UNICODE_DATA_FILE)))) {
            while (true) {
                final String line = in.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("//")) {
                    maybeDebug(line);
                } else {
                    final Matcher matcher = CHAR_MAPPING_PAIR.matcher(line);
                    if (matcher.matches()) {
                        final int xccsValue = Integer.parseInt(matcher.group(1), 16);
                        final int unicodeValue = Integer.parseInt(matcher.group(2), 16);
                        xccsToUnicode.put(xccsValue, unicodeValue);
                        xccsCharsetToCodes.computeIfAbsent(charset(xccsValue), key -> new TreeSet<>()).add(xccsValue);
                    }
                }
            }
        }
    }

    private void loadCharsetNameData(File fromDir) throws IOException {
        try (final BufferedReader in = new BufferedReader(new FileReader(new File(fromDir, XCCS_CHARSET_NAMES_DATA_FILE)))) {
            while (true) {
                final String line = in.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("//")) {
                    maybeDebug(line);
                } else {
                    final Matcher matcher = CHARSET_NAME_EXTRACTOR.matcher(line);
                    if (matcher.matches()) {
                        final int charset = Integer.parseInt(matcher.group(1));
                        final String name = matcher.group(2);
                        xccsCharsetToName.put(charset, name);
                    }
                }
            }
        }
    }

    private void maybeDebug(String line) {
        if (debug) {
            LOG.warn("Comment: {}", line);
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
     * Given an XCCS charset, return its name.  If the charset doesn't exist or its name is unknown,
     * return null.
     *
     * @param charset the charset number
     * @return the name, if known, or null
     */
    public String charsetName(int charset) {
        return xccsCharsetToName.get(charset);
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

