# Medley Web Font Metrics Extractor

This is software that generates font metrics files suitable for the Medley `HTMLSTREAM` device-independent
graphics implementation.

## Character Encoding

Medley uses Xerox Character Code Standard (XCCS) 2.0+ to represent characters.
XCCS is one of the predecessors of, and a major influence on, the Unicode
standard.  XCCS can represent up to 65,535 characters and organizes them in 256-character sets.

Unicode is able to represent many more characters than XCCS.  The majority of the character glyphs defined in XCCS
are also present in Unicode, though there are exceptions.

## Supported Fonts

We opted not to create our own fonts, but rather to select open source fonts created by others.

We've chosen fonts capable of representing as large a set of XCCS characters as possible.  Since there are no XCCS-native fonts
suitable for use on the Web, in practice, this means we're choosing fonts that cover as much of the _Unicode_ character space as
possible.  We also desire that our fonts include multiple _faces_, and consider serif, sans serif, monospaced, and display
as the basic set.

Based on these criteria, we focussed on Google's font collection.  We had a brief dalliance with [Roboto](https://fonts.google.com/specimen/Roboto/about) before realizing
its shortcomings (principally that it covers a small portion of the Unicode and XCCS code spaces than we'd like), we settled on
[Noto](https://fonts.google.com/noto).

We've defined 4 Medley font families based on Noto:
1. `NONO-SANS`
2. `NOTO-SANS-MONO`
3. `NOTO-SANS-DISPLAY`
4. `NOTO-SERIF`

Each family is available in these styles:
1. Plain
2. Italic
3. Bold
4. Bold Italic

Each face+style is available in these point sizes:
8, 10, 12, 14, 16, 18, 20, 24, 32, 40, 92

## Prerequisites

- An Internet connection.  The tool downloads its fonts as _webfonts_ when it starts up.
- A Java development kit (JDK), version 21.

## Generating font metrics files

Class `Main` provides the font metrics generator (and other tools too).

To run it, create a destination directory for the metrics files (here `/font/destination/directory`):

````bash
$ mkdir /font/destination/directory
````

You don't need to download any font files: the software accesses its fonts as _webfonts_ over the Internet.

Simply run the font metrics generator:
````bash
$ java -cp WebFontMetrics.main org.interlisp.Main -d /font/destination/directory
````

The process will generate a large number of files, one for each combination of
family X style X size X XCCS character sets, plus one table of contents for each combination of family X style X size.

## Viewing font coverage

To see what portion of the XCCS code space a given set of font components (a "font stack") covers, tun

````bash
$ java -cp WebFontMetrics.main org.interlisp.FontCoverage
````
