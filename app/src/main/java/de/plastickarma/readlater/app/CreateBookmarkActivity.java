package de.plastickarma.readlater.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.common.base.Joiner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Main Activity to add a bookmark.
 */
public final class CreateBookmarkActivity extends ActionBarActivity {

    private static final String SAVED_TITLE_KEY = "savedTitle";
    private static final String SAVED_URL_KEY = "savedURL";
    private static final String SAVED_DESCRIPTION_KEY = "savedDescription";
    private static final String SAVED_CATEGORIES_KEY = "savedCategories";

    private List<CategoryMapping> categoryMappings;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_later_main);
        // Load mappings once, so we don't have to look them up all the time
        categoryMappings = CategoryMapping.getCategoryMappings(this);

        final EditText titleTextInput = (EditText) findViewById(R.id.bookmarkTitleInput);
        final EditText urlTextInput = (EditText) findViewById(R.id.bookmarkURLInput);
        final EditText descriptionTextInput = (EditText) findViewById(R.id.bookmarkDescriptionInput);
        final EditText categoriesTextInput = (EditText) findViewById(R.id.bookmarkCategoriesInput);

        // Focus listener to set categories, when leaving the text field.
        final View.OnFocusChangeListener autoCategoryInserter = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (!hasFocus) {
                    String categories = getCategories(
                            ActivityHelper.getNullCheckedText(urlTextInput) +
                            ActivityHelper.getNullCheckedText(titleTextInput) +
                            ActivityHelper.getNullCheckedText(descriptionTextInput)
                    );
                    categoriesTextInput.setText(categories);
                }
            }
        };
        titleTextInput.setOnFocusChangeListener(autoCategoryInserter);
        urlTextInput.setOnFocusChangeListener(autoCategoryInserter);
        descriptionTextInput.setOnFocusChangeListener((autoCategoryInserter));


        // Try to fill in the text fields if we come from a share intent.
        final Intent intent = this.getIntent();
        final String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_SEND)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            final Bookmark bookmark = BookmarkExtractor.extractBookmark(text);
            if (bookmark == null) {
                urlTextInput.setText(text);
            } else {
                titleTextInput.setText(bookmark.getTitle());
                urlTextInput.setText(bookmark.getUrl());
            }
            String categories = getCategories(text);
            categoriesTextInput.setText(categories);

        // Try to fill in the text fields from the saved state
        } else  {
            SharedPreferences pref = getPreferences(MODE_PRIVATE);
            titleTextInput.setText(pref.getString(SAVED_TITLE_KEY, ""));
            urlTextInput.setText(pref.getString(SAVED_URL_KEY, ""));
            descriptionTextInput.setText(pref.getString(SAVED_DESCRIPTION_KEY, ""));
            categoriesTextInput.setText(pref.getString(SAVED_CATEGORIES_KEY, ""));
        }


        final Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Button b = (Button) v;
                b.setEnabled(false);
                b.setText(getString(R.string.buttonTextWhileSaving));
                Bookmark bm = new Bookmark(
                        ActivityHelper.getNullCheckedText(urlTextInput),
                        ActivityHelper.getNullCheckedText(titleTextInput),
                        ActivityHelper.getNullCheckedText(descriptionTextInput),
                        ActivityHelper.getNullCheckedText(categoriesTextInput));
                RemoteBookmarkCreator.createRemoteBookmark(
                        v.getContext(),
                        Settings.getOwncloudURL(v.getContext()),
                        Settings.getOwncloudUser(v.getContext()),
                        Settings.getOwncloudPassword(v.getContext()),
                        bm,
                        new Runnable() {
                            @Override
                            public void run() {
                                b.setEnabled(true);
                                b.setText(getString(R.string.saveBookmarkButtonText));
                                clearInputFields();
                            }
                        }
                );
            }
        });
        final Button clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                clearInputFields();
            }
        });
    }

    private void clearInputFields() {
        ((EditText) findViewById(R.id.bookmarkTitleInput)).setText("");
        ((EditText) findViewById(R.id.bookmarkURLInput)).setText("");
        ((EditText) findViewById(R.id.bookmarkDescriptionInput)).setText("");
        ((EditText) findViewById(R.id.bookmarkCategoriesInput)).setText("");
    }

    /**
     * Determines the categories from the given text
     * @return A string, in which the categories are comma-separated
     */
    private String getCategories(String text) {
        final Set<String> categories = new HashSet<String>();
        for (CategoryMapping m : this.categoryMappings) {
            if (text.contains(m.getText())) {
                categories.add(m.getCategories());
            }
        }
        if (categories.isEmpty()) {
            categories.add(Settings.getOwncloudDefaultCategory(this));
        }
        return Joiner.on(',').join(categories);
    }

    @Override
    protected void onPause() {
        super.onPause();
        final EditText titleTextInput = (EditText) findViewById(R.id.bookmarkTitleInput);
        final EditText urlTextInput = (EditText) findViewById(R.id.bookmarkURLInput);
        final EditText descriptionTextInput = (EditText) findViewById(R.id.bookmarkDescriptionInput);
        final EditText categoriesTextInput = (EditText) findViewById(R.id.bookmarkCategoriesInput);


        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        final SharedPreferences.Editor edit = pref.edit();
        edit.putString(SAVED_TITLE_KEY, ActivityHelper.getNullCheckedText(titleTextInput));
        edit.putString(SAVED_URL_KEY, ActivityHelper.getNullCheckedText(urlTextInput));
        edit.putString(SAVED_DESCRIPTION_KEY, ActivityHelper.getNullCheckedText(descriptionTextInput));
        edit.putString(SAVED_CATEGORIES_KEY, ActivityHelper.getNullCheckedText(categoriesTextInput));
        edit.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.read_later_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_categories) {
            Intent intent = new Intent(this, CategoryMappingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
