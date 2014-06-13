package de.plastickarma.readlater.app;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BookmarkCreator {


    private static final String ADD_BM_LOCATION =
            "index.php?redirect_url=%2Fowncloud%2Findex.php%2Fapps%2Fbookmarks%2FaddBm.php";
    private static final String EDIT_BM_LOCATION =
            "index.php/apps/bookmarks/ajax/editBookmark.php";

    private static Pattern REQUEST_TOKEN_PATTERN = Pattern.compile("name=\"requesttoken\"\\s*value=\"(.*)\"");

    /**
     * Saves the given bookmark information to a remote owncloud instance.
     * @param baseUrl the url for the owncloud instance.
     * @param owncloudUser the user for the owncloud instance
     * @param owncloudPass the password for the owncloud user
     * @param bookmark the bookmark, that shall be saved.
     */
    public static void createRemoteBookmark(
        final Context context,
        final String baseUrl,
        final String owncloudUser,
        final String owncloudPass,
        final Bookmark bookmark) {

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, createNotification(
                context,
                context.getString(R.string.savingBookmarkNotTitle),
                context.getString(R.string.savingBookmarkNotText),
                android.R.drawable.stat_sys_upload)
        );

        Ion.with(context, makeUrl(baseUrl, ADD_BM_LOCATION))
                .setBodyParameter("user", owncloudUser)
                .setBodyParameter("password", owncloudPass)
                .setBodyParameter("password-clone", owncloudPass)
                .setBodyParameter("timezone-offset", "1")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            signalError(
                                    context,
                                    context.getString(R.string.badResponseTitle),
                                    context.getString(R.string.savingBookmarkErrorText),
                                    e);
                            Log.d("Bad Response", "HTTP result: " + result);
                            return;
                        }

                        final Matcher matcher = REQUEST_TOKEN_PATTERN.matcher(result);
                        if (!matcher.find()) {
                            signalError(
                                    context,
                                    context.getString(R.string.badResponseTitle),
                                    context.getString(R.string.savingBookmarkBadResponseText),
                                    null);
                            Log.d("Bad Response", result);
                            return;
                        }
                        final String requestToken = matcher.group(1);

                        Ion.with(context, makeUrl(baseUrl, EDIT_BM_LOCATION))
                                .setBodyParameter("requesttoken", requestToken)
                                .setBodyParameter("record_id", "")
                                .setBodyParameter("description", bookmark.getDescription())
                                .setBodyParameter("title", bookmark.getTitle())
                                .setBodyParameter("url", bookmark.getUrl())
                                .setBodyParameter("item[tags][]", bookmark.getCategories())
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        if (e != null) {
                                            signalError(
                                                    context,
                                                    context.getString(R.string.badResponseTitle),
                                                    context.getString(R.string.savingBookmarkErrorText),
                                                    e);
                                            Log.d("Bad Response", "HTTP result: " + result);
                                            return;
                                        }
                                        notificationManager.notify(0, createNotification(
                                                        context,
                                                        context.getString(R.string.savingBookmarkNotTitle),
                                                        context.getString(R.string.savingBookmarkDoneNotText),
                                                        android.R.drawable.stat_sys_upload_done)
                                        );
                                        //TODO evaluate response from owncloud
                                    }
                                });
                    }
                });
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

    private static void signalError(
            final Context context,
            final String title,
            final String message,
            final Exception exception) {

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (exception != null) {
            Log.e(title, message, exception);
        } else {
            Log.e(title, message);
        }

        notificationManager.notify(
                0,
                createNotification(
                        context,
                        title,
                        exception != null ? exception.getMessage() : message,
                        android.R.drawable.stat_notify_error)
        );
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {

            }
        });
        ab.setTitle("Something went wrong").setMessage(exception != null ? exception.getMessage() : message);
        ab.create().show();
    }

    private static String makeUrl(String base, String location) {
        final boolean noSlash = base.endsWith("/") || location.startsWith("/");
        return String.format("%s%s%s",base, noSlash ? "" : "/", location);
    }
}
