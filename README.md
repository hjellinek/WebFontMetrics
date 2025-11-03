# Medley Web Font Metrics Extractor

This is software that generates font metrics files suitable for the Medley `HTMLSTREAM` device-independent
graphics implementation.

## Character Encoding

Medley's characters are encoded in the Xerox Character Code Standard (XCCS) 2.0+, slightly updated to
rearrange a couple of codes to better correspond with modern usage.  We call this augmented
encoding the Medley Character Code Standard - MCCS.

XCCS was one of the predecessors of, and a major influence on, the Unicode
standard.  XCCS/MCCS/ can represent up to 65,535 characters, and organizes them in 256-character sets.

Unicode is able to represent many more characters than XCCS/MCCS.  The majority of the character glyphs
defined are also present in Unicode, though there are exceptions.

## Supported Fonts

We opted not to create our own fonts, but rather to select open source fonts created by others.

We've chosen fonts capable of representing as large a set of MCCS characters as possible.  Since there are no MCCS-native fonts
suitable for use on the Web, in practice, this means we're choosing fonts that cover as much of the _Unicode_ character space as
possible.  We also desire that our fonts include multiple _faces_, and consider serif, sans serif, monospaced, and display
as the basic set.

Based on these criteria, we focussed on Google's font collection.  We had a brief dalliance with [Roboto](https://fonts.google.com/specimen/Roboto/about) before realizing
its shortcomings (principally that it covers a small portion of the Unicode and MCCS code spaces than we'd like), we settled on
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
6, 8, 10, 12, 14, 16, 18, 20, 24, 32, 34, 36, 38, 40, 42, 44, 46, 48, 50, 92.

## Prerequisites

- An Internet connection.  The tool downloads its fonts as _webfonts_ when it starts up.
- A Java development kit (JDK), version 21.

## Generating the MCCS-to-Unicode mapping table

The files `mccs_charset_names.txt` and `mccs_to_unicode.txt` in `resources/data` provide the MCCS
data we need.  We created  `mccs_charset_names.txt` by hand from the XCCS standard.  We generated
`mccs_to_unicode.txt` using the Interlisp package `XCCS-UNICODE-DUMPER` and calling the function
`(WRITE-M-TO-U-MAPPING-TABLE :OUTPUT-DIR "{DSK}<some>dir>")`.

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
family X style X size X MCCS character sets, plus one table of contents for each combination of family X style X size.

## Viewing font coverage

To see what portion of the MCCS code space a given set of font components (a "font stack") covers, tun

````bash
$ java -cp WebFontMetrics.main org.interlisp.FontCoverage
````
