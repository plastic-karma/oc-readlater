package de.plastickarma.readlater.app;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class to signal if a list of EditTexts all contain some text.
 */
public abstract class RequireNonEmptyTextWatcher {

    private final List<EditText> toBeWatched;
    private final Set<EditText> currentlyNonEmpty = new HashSet<EditText>();

    public RequireNonEmptyTextWatcher(EditText ...toBeWatched) {
        this.toBeWatched = Arrays.asList(toBeWatched);

        for (final EditText text : this.toBeWatched) {
            text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                }

                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                }

                @Override
                public void afterTextChanged(final Editable s) {
                    if (s.length() > 0) {
                        if(currentlyNonEmpty.add(text)) {
                            doNotify();
                        }
                    } else {
                        if (currentlyNonEmpty.remove(text)) {
                            doNotify();
                        }
                    }
                }
            });
        }
    }

    /**
     * Signals, that all watched EditTexts contain text.
     */
    public abstract void allAreNonEmpty();

    /**
     * Signals, that at least one watched EditText does not contain any text.
     */
    public abstract void someAreEmpty();

    private void doNotify() {
        if (currentlyNonEmpty.containsAll(toBeWatched)) {
            this.allAreNonEmpty();
        } else {
            someAreEmpty();
        }
    }
}
