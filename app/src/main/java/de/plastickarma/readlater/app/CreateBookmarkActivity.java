package de.plastickarma.readlater.app;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.google.common.base.Joiner;
import com.koushikdutta.ion.builder.Builders;

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

    private static final String EDIT_BM_LOCATION =
            "index.php/apps/bookmarks/ajax/editBookmark.php";

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

        // Save what the user has typed so far
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
        } else if (id == R.id.action_save_bookmark) {

            final View oldItemView = item.getActionView();

            new OwncloudRequest(
                    Settings.getOwncloudURL(this),
                    Settings.getOwncloudUser(this),
                    Settings.getOwncloudPassword(this)) {

                @Override
                protected void onBeforeExecute(final Context context) {
                    item.setEnabled(false);
                    item.setActionView(new ProgressBar(context));
                    notifyIfEnabled(
                            context,
                            context.getString(R.string.savingBookmarkNotTitle),
                            context.getString(R.string.savingBookmarkNotText),
                            android.R.drawable.stat_sys_upload);
                }

                @Override
                protected void onSuccess(final Context context, final String result) {
                    item.setEnabled(true);
                    item.setActionView(oldItemView);
                    clearInputFields();
                    notifyIfEnabled(
                            context,
                            context.getString(R.string.savingBookmarkNotTitle),
                            context.getString(R.string.savingBookmarkDoneNotText),
                            android.R.drawable.stat_sys_upload_done);
                }

                @Override
                protected void onError(final Context context, final Exception e) {
                    item.setEnabled(true);
                    item.setActionView(oldItemView);
                    notifyIfEnabled(
                            context,
                            context.getString(R.string.badResponseTitle),
                            e.getMessage(),
                            android.R.drawable.stat_notify_error);
                    AlertDialog.Builder ab = new AlertDialog.Builder(context);
                    ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {

                        }
                    });
                    ab.setTitle(context.getString(R.string.savingBookmarkErrorText)).setMessage(e.getMessage());
                    ab.create().show();
                }

                @Override
                protected void modifyRequest(final Builders.Any.B requestBuilder) {
                    final EditText titleTextInput = (EditText) findViewById(R.id.bookmarkTitleInput);
                    final EditText urlTextInput = (EditText) findViewById(R.id.bookmarkURLInput);
                    final EditText descriptionTextInput = (EditText) findViewById(R.id.bookmarkDescriptionInput);
                    final EditText categoriesTextInput = (EditText) findViewById(R.id.bookmarkCategoriesInput);

                    final Bookmark bookmark = new Bookmark(
                            correctURLifNecessary(ActivityHelper.getNullCheckedText(urlTextInput)),
                            ActivityHelper.getNullCheckedText(titleTextInput),
                            ActivityHelper.getNullCheckedText(descriptionTextInput),
                            ActivityHelper.getNullCheckedText(categoriesTextInput));

                    requestBuilder
                            .setBodyParameter("record_id", "")
                            .setBodyParameter("description", bookmark.getDescription())
                            .setBodyParameter("title", bookmark.getTitle())
                            .setBodyParameter("url", bookmark.getUrl())
                            .setBodyParameter("item[tags][]", bookmark.getCategories());
                }


            }. execute(this, EDIT_BM_LOCATION);
        }
        return super.onOptionsItemSelected(item);
    }

    private static void notifyIfEnabled(
            final Context context,
            final String title,
            final String message,
            final int icon) {
        if (Settings.notificationsAreEnabled(context)) {
            final NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, createNotification(context, title, message, icon));
        }
    }

    private static String correctURLifNecessary(final String bookmark) {
        if (bookmark.startsWith("http")) {
            return bookmark;
        } else {
            return "http://" + bookmark;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static Notification createNotification(
            final Context context, final String title, final String message, final int icon) {
        return new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(icon)
                .build();
    }
}
