package de.plastickarma.readlater.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Main Activity to add a bookmark.
 */
public final class ReadLaterMain extends ActionBarActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_later_main);

        final EditText titleTextInput = (EditText) findViewById(R.id.bookmarkTitleInput);
        final EditText urlTextInput = (EditText) findViewById(R.id.bookmarkURLInput);
        final EditText descriptionTextInput = (EditText) findViewById(R.id.bookmarkDescriptionInput);
        final EditText categoriesTextInput = (EditText) findViewById(R.id.bookmarkCategoriesInput);

        final View.OnFocusChangeListener autoCategoryInserter = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (!hasFocus) {
                    String categories = getCategories(
                            ActivityHelper.getNullCheckedText(urlTextInput) +
                            ActivityHelper.getNullCheckedText(titleTextInput) +
                            ActivityHelper.getNullCheckedText(descriptionTextInput),
                            v.getContext()
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
                titleTextInput.setText(bookmark.getDescription());
                urlTextInput.setText(bookmark.getUrl());
            }
            String categories = getCategories(text, this);
            categoriesTextInput.setText(categories);
        }


        final Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
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
                        bm
                );
            }
        });
    }

    public static String getCategories(String text, Context ctx) {

        final List<CategoryMapping> categoryMappings = CategoryMapping.getCategoryMappings(ctx);
        final Set<String> categories = new HashSet<String>();
        for (CategoryMapping m : categoryMappings) {
            if (text.contains(m.getText())) {
                categories.add(m.getCategories());
            }
        }
        if (categories.isEmpty()) {
            categories.add(Settings.getOwncloudDefaultCategory(ctx));
        }
        return implode(categories);
    }

    private static String implode(Set<String> s) {
        StringBuilder result = new StringBuilder();
        final Iterator<String> iterator = s.iterator();
        while (iterator.hasNext()) {
            result.append(iterator.next());
            if (iterator.hasNext()) {
                result.append(',');
            }
        }
        return result.toString();
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
