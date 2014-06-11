package de.plastickarma.readlater.app;

import android.widget.EditText;

/**
 * Utility functions.
 */
public final class ActivityHelper {

    private ActivityHelper() { }

    /**
     * Gets the text from an EditText. Returns null, if no text is set.
     */
    static String getNullCheckedText(final EditText text) {
        return text.getText() == null ? "" : text.getText().toString();
    }
}
