package de.plastickarma.readlater.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


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
        }
        categoriesTextInput.setText(Settings.getOwncloudDefaultCategory(this));

        final Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Bookmark bm = new Bookmark(
                        ActivityHelper.getNullCheckedText(urlTextInput),
                        ActivityHelper.getNullCheckedText(titleTextInput),
                        ActivityHelper.getNullCheckedText(descriptionTextInput),
                        ActivityHelper.getNullCheckedText(categoriesTextInput));
                BookmarkCreator.createRemoteBookmark(
                        v.getContext(),
                        Settings.getOwncloudURL(v.getContext()),
                        Settings.getOwncloudUser(v.getContext()),
                        Settings.getOwncloudPassword(v.getContext()),
                        bm
                );
            }
        });
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
