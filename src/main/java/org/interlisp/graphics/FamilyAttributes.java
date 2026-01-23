/*
 *
 * Copyright 2026 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.graphics;

public class FamilyAttributes {

    public enum Weight {
        BOLD('B'), MEDIUM('M'), LIGHT('L');

        private final char abbrev;

        Weight(char abbrev) {
            this.abbrev = abbrev;
        }

        public char getAbbrev() {
            return abbrev;
        }

        public char getLcAbbrev() {
            return Character.toLowerCase(abbrev);
        }
    }

    public enum Slope {
        ITALIC('I'), REGULAR('R');

        private final char abbrev;

        Slope(char abbrev) {
            this.abbrev = abbrev;
        }

        public char getAbbrev() {
            return abbrev;
        }

        public char getLcAbbrev() {
            return Character.toLowerCase(abbrev);
        }
    }

    public enum Expansion {
        REGULAR('R'), COMPRESSED('C'), EXPANDED('E');

        private final char abbrev;

        Expansion(char abbrev) {
            this.abbrev = abbrev;
        }

        public char getAbbrev() {
            return abbrev;
        }

        public char getLcAbbrev() {
            return Character.toLowerCase(abbrev);
        }
    }

}
