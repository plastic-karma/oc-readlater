package de.plastickarma.readlater.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.CheckBox;
import android.widget.EditText;


/**
 * Activity to alter and save settings.
 */
public final class Settings extends ActionBarActivity {

    final static String SHARED_PREF_KEY = "de.plastickarma.readlater.shared";

    private final static String URL_KEY  = "url";

    private final static String USER_KEY = "user";

    private final static String PASSWORD_KEY = "password";

    private final static String DEFAULT_CAT_KEY = "defaultCat";

    private final static String ENABLE_NOT_KEY = "enableNotifications";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final EditText owncloudURL = (EditText) findViewById(R.id.owncloudURLTextInput);
        final EditText owncloudUser = (EditText) findViewById(R.id.owncloudUserTextInput);
        final EditText owncloudPassword = (EditText) findViewById(R.id.owncloudPasswordTextInput);
        final EditText owncloudDefaultCategory = (EditText) findViewById(R.id.owncloudDefaultCategoryTextInput);
        final CheckBox enableNotificationsBox = (CheckBox) findViewById(R.id.enableNotifications);

        owncloudURL.setText(getOwncloudURL(this));
        owncloudUser.setText(getOwncloudUser(this));
        owncloudPassword.setText(getOwncloudPassword(this));
        owncloudDefaultCategory.setText(getOwncloudDefaultCategory(this));
        enableNotificationsBox.setChecked(notificationsAreEnabled(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        final EditText owncloudURL = (EditText) findViewById(R.id.owncloudURLTextInput);
        final EditText owncloudUser = (EditText) findViewById(R.id.owncloudUserTextInput);
        final EditText owncloudPassword = (EditText) findViewById(R.id.owncloudPasswordTextInput);
        final EditText owncloudDefaultCategory = (EditText) findViewById(R.id.owncloudDefaultCategoryTextInput);
        final CheckBox enableNotificationsBox = (CheckBox) findViewById(R.id.enableNotifications);

        final SharedPreferences settings = getSettings(this);
        final SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putString(URL_KEY, ActivityHelper.getNullCheckedText(owncloudURL));
        settingsEditor.putString(USER_KEY, ActivityHelper.getNullCheckedText(owncloudUser));
        settingsEditor.putString(PASSWORD_KEY, ActivityHelper.getNullCheckedText(owncloudPassword));
        settingsEditor.putString(DEFAULT_CAT_KEY, ActivityHelper.getNullCheckedText(owncloudDefaultCategory));
        settingsEditor.putBoolean(ENABLE_NOT_KEY, enableNotificationsBox.isChecked());
        settingsEditor.commit();
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

    public static boolean notificationsAreEnabled(final Context context) {
        return getSettings(context).getBoolean(ENABLE_NOT_KEY, true);
    }
}
