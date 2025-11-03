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
 * Map MCCS (XCCS 2.0.0) to Unicode.
 */
public class MccsToUnicode {

    private static final Logger LOG = LoggerFactory.getLogger(MccsToUnicode.class);

    private static final String MCCS_TO_UNICODE_DATA_FILE = "mccs_to_unicode.txt";

    private static final String MCCS_CHARSET_NAMES_DATA_FILE = "mccs_charset_names.txt";

    private static final Pattern CHAR_MAPPING_PAIR =
            Pattern.compile("0x(\\p{XDigit}\\p{XDigit}\\p{XDigit}\\p{XDigit}) *0x(\\p{XDigit}\\p{XDigit}\\p{XDigit}\\p{XDigit})");

    private static final Pattern CHARSET_NAME_EXTRACTOR =
            Pattern.compile("\\d+ +(\\d+) +(.*) *$");

    private final boolean debug;

    private final Map<Integer, Integer> mccsToUnicode = new HashMap<>(12000);

    private final Map<Integer, String> mccsCharsetToName = new HashMap<>(120);

    private final Map<Integer, SortedSet<Integer>> mccsCharsetToCodes = new HashMap<>(256);

    /**
     * The code for the Unicode <a href="https://en.wikipedia.org/wiki/Specials_(Unicode_block)#Replacement_character">REPLACEMENT CHARACTER</a>.
     */
    public static final char REPLACEMENT_CHAR = 0xFFFD;

    private static MccsToUnicode SINGLETON = null;

    public synchronized static void init(File fromDir) {
        if (SINGLETON == null) {
            try {
                SINGLETON = new MccsToUnicode(fromDir);
            } catch (IOException e) {
                LOG.error("", e);
            }
        }
    }

    public static MccsToUnicode getInstance() {
        return SINGLETON;
    }

    /**
     * Load the data into mccsToUnicode and x=mccsCharsetToCodes.
     *
     * @param fromDir the directory holding the file
     * @param debug   if true, dump comments to the log
     */
    public MccsToUnicode(File fromDir, boolean debug) throws IOException {
        this.debug = debug;
        loadMappingData(fromDir);
        loadCharsetNameData(fromDir);
    }

    /**
     * Load the data into mccsToUnicode and mccsCharsetToCodes.  Don't dump comments to the log.
     *
     * @param fromDir the directory holding the file
     */
    public MccsToUnicode(File fromDir) throws IOException {
        this(fromDir, false);
    }

    /**
     * Return the charset portion of the MCCS code.
     *
     * @param mccsCode the MCCS code
     * @return the charset
     */
    public static int charset(int mccsCode) {
        return (mccsCode >> 8) & 0xFF;
    }

    /**
     * Return the character code portion of the MCCS code.
     *
     * @param mccsCode the MCCS code
     * @return the character code
     */
    public static int charCode(int mccsCode) {
        return mccsCode & 0xFF;
    }

    private void loadMappingData(File fromDir) throws IOException {
        try (final BufferedReader in = new BufferedReader(new FileReader(new File(fromDir, MCCS_TO_UNICODE_DATA_FILE)))) {
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
                        final int mccsValue = Integer.parseInt(matcher.group(1), 16);
                        final int unicodeValue = Integer.parseInt(matcher.group(2), 16);
                        mccsToUnicode.put(mccsValue, unicodeValue);
                        mccsCharsetToCodes.computeIfAbsent(charset(mccsValue), key -> new TreeSet<>()).add(mccsValue);
                    }
                }
            }
        }
    }

    private void loadCharsetNameData(File fromDir) throws IOException {
        try (final BufferedReader in = new BufferedReader(new FileReader(new File(fromDir, MCCS_CHARSET_NAMES_DATA_FILE)))) {
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
                        mccsCharsetToName.put(charset, name);
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
     * Return the Unicode value for the given MCCS code.  The result may be null.
     *
     * @return the corresponding Unicode character
     */
    public Integer unicode(int mccs) {
        return mccsToUnicode.get(mccs);
    }

    /**
     * For a given MCCS charset, return the MCCS characters that belong to it (that map to Unicode).
     *
     * @param mccsCharset the MCCS charset
     * @return an unmodifiable set of the constituent characters, sorted
     */
    public SortedSet<Integer> charsetMembers(int mccsCharset) {
        return Collections.unmodifiableSortedSet(mccsCharsetToCodes.get(mccsCharset));
    }

    /**
     * Return all charsets, sorted.
     *
     * @return all charsets
     */
    public SortedSet<Integer> charsets() {
        return Collections.unmodifiableSortedSet(new TreeSet<>(mccsCharsetToCodes.keySet()));
    }

    /**
     * Return true if we support the given MCCS charset.
     *
     * @param charset the charset to check
     * @return true if we support it
     */
    public boolean supportsCharset(int charset) {
        return mccsCharsetToCodes.containsKey(charset);
    }

    /**
     * Return the number of character sets we support.
     *
     * @return the number of character sets we support
     */
    public int numCharsets() {
        return mccsCharsetToCodes.size();
    }

    /**
     * Given an MCCS charset, return its name.  If the charset doesn't exist or its name is unknown,
     * return null.
     *
     * @param charset the charset number
     * @return the name, if known, or null
     */
    public String charsetName(int charset) {
        return mccsCharsetToName.get(charset);
    }

    /**
     * Return the number of MCCS characters we've mapped to Unicode.
     *
     * @return the number of MCCS characters we've mapped to Unicode
     */
    public int numCharacters() {
        return mccsToUnicode.size();
    }
}

