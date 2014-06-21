package de.plastickarma.readlater.app;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                        currentlyNonEmpty.add(text);
                    } else {
                        currentlyNonEmpty.remove(text);
                    }
                    checkNotify();

                }
            });
        }
    }

    public abstract void allAreNonEmpty();

    public abstract void someAreEmpty();

    private void checkNotify() {
        if (currentlyNonEmpty.containsAll(toBeWatched)) {
            this.allAreNonEmpty();
        } else {
            someAreEmpty();
        }
    }
}
