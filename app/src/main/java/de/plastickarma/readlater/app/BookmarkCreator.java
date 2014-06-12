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


    private static String ADD_BM_LOCATION = "index.php?redirect_url=%2Fowncloud%2Findex.php%2Fapps%2Fbookmarks%2FaddBm.php";
    private static String EDIT_BM_LOCATION = "index.php/apps/bookmarks/ajax/editBookmark.php";

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
            context, "Saving bookmark", "Bookmark will be saved to owncloud...", android.R.drawable.stat_sys_upload));
        Ion.with(context, makeUrl(baseUrl, ADD_BM_LOCATION))
                .setBodyParameter("user", owncloudUser)
                .setBodyParameter("password", owncloudPass)
                .setBodyParameter("password-clone", owncloudPass)
                .setBodyParameter("timezone-offset", "1")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        //TODO Refactor createMessageOnException and error handling in general
                        final AlertDialog exceptionMessage = createMessageOnException(e, result, context);
                        if (exceptionMessage != null) {
                            Log.e("Bad Response", "Error while saving bookmark", e);
                            notificationManager.notify(0, createNotification(context, "Saving bookmark",
                                    "Error while communicating with owncloud: " + e.getMessage(), android.R.drawable.stat_notify_error));
                            exceptionMessage.show();
                            return;
                        }

                        final Matcher matcher = REQUEST_TOKEN_PATTERN.matcher(result);
                        if (!matcher.find()) {
                            Log.e("Bad Response", "Could not parse request token from response");
                            Log.e("Bad Response", result);
                            notificationManager.notify(0, createNotification(
                                    context, "Saving bookmark", "Bad response from owncloud instance", android.R.drawable.stat_sys_upload));
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

                                        final AlertDialog exceptionMessage = createMessageOnException(e, result, context);
                                        if (exceptionMessage != null) {
                                            Log.e("Bad Response", "Error while saving bookmark", e);
                                            notificationManager.notify(0, createNotification(context, "Saving bookmark",
                                                    "Error while communicating with owncloud: " + e.getMessage(), android.R.drawable.stat_notify_error));
                                            exceptionMessage.show();
                                        } else {
                                            notificationManager.notify(0, createNotification(context, "Saving bookmark",
                                                    "Bookmark successfully saved", android.R.drawable.stat_sys_upload_done));
                                        }
                                        //TODO evaluate response from owncloud
                                    }
                                });
                    }
                });
    }

    private static AlertDialog createMessageOnException(final Exception e, final String result, final Context context) {
        if (e != null || result == null) {
            AlertDialog.Builder ab = new AlertDialog.Builder(context);
            ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {

                }
            });
            ab.setTitle("Something went wrong").setMessage(e.getMessage());
            return ab.create();
        }
        return null;
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

    private static String makeUrl(String base, String location) {
        final boolean noSlash = base.endsWith("/") || location.startsWith("/");
        return String.format("%s%s%s",base, noSlash ? "" : "/", location);
    }
}
