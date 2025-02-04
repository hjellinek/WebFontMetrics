/*
 *
 * Copyright 2025 by Herb Jellinek.  All rights reserved.
 *
 */
package org.interlisp.graphics;

import java.awt.*;

public class FontUtils {

    /**
     * You can't instantiate one of these.
     */
    private FontUtils() {
    }

    public static String describe(Font f) {
        final String strStyle;

        if (f.isBold()) {
            strStyle = f.isItalic() ? "bold italic" : "bold";
        } else {
            strStyle = f.isItalic() ? "italic" : "plain";
        }

        return "family=" + f.getFamily() + ", name=" + f.getName() + ", style=" +
                strStyle + ", size=" + f.getSize();
    }
}

