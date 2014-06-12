package de.plastickarma.readlater.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Activity to alter and save settings.
 */
public final class Settings extends ActionBarActivity {

    private final static String SHARED_PREF_KEY = "de.plastickarma.readlater.shared";

    private final static String URL_KEY  = "url";

    private final static String USER_KEY = "user";

    private final static String PASSWORD_KEY = "password";

    private final static String DEFAULT_CAT_KEY = "defaultCat";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final EditText owncloudURL = (EditText) findViewById(R.id.owncloudURLTextInput);
        final EditText owncloudUser = (EditText) findViewById(R.id.owncloudUserTextInput);
        final EditText owncloudPassword = (EditText) findViewById(R.id.owncloudPasswordTextInput);
        final EditText owncloudDefaultCategory = (EditText) findViewById(R.id.owncloudDefaultCategoryTextInput);

        owncloudURL.setText(getOwncloudURL(this));
        owncloudUser.setText(getOwncloudUser(this));
        owncloudPassword.setText(getOwncloudPassword(this));
        owncloudDefaultCategory.setText(getOwncloudDefaultCategory(this));

        final Button saveSettingsButton = (Button) findViewById(R.id.saveSettingsButton);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final SharedPreferences settings = getSettings(v.getContext());
                final SharedPreferences.Editor settingsEditor = settings.edit();
                settingsEditor.putString(URL_KEY, ActivityHelper.getNullCheckedText(owncloudURL));
                settingsEditor.putString(USER_KEY, ActivityHelper.getNullCheckedText(owncloudUser));
                settingsEditor.putString(PASSWORD_KEY, ActivityHelper.getNullCheckedText(owncloudPassword));
                settingsEditor.putString(DEFAULT_CAT_KEY, ActivityHelper.getNullCheckedText(owncloudDefaultCategory));
                settingsEditor.commit();
            }
        });
    }

    private static SharedPreferences getSettings(final Context context) {
        return context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
    }

    public static String getOwncloudURL(final Context context) {
        return getSettings(context).getString(URL_KEY, "");
    }

    public static String getOwncloudUser(final Context context) {
        return getSettings(context).getString(USER_KEY, "");
    }

    public static String getOwncloudPassword(final Context context) {
        return getSettings(context).getString(PASSWORD_KEY, "");
    }
    public static String getOwncloudDefaultCategory(final Context context) {
        return getSettings(context).getString(DEFAULT_CAT_KEY, "");
    }
}