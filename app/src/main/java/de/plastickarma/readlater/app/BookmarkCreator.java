package de.plastickarma.readlater.app;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
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

        Notification notification = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle("saving bookmark...")
                .setContentText("Saving bookmark to owncloud instance")
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .build();

        notificationManager.notify(0, notification);
        Ion.with(context, makeUrl(baseUrl, ADD_BM_LOCATION))
                .setBodyParameter("user", owncloudUser)
                .setBodyParameter("password", owncloudPass)
                .setBodyParameter("password-clone", owncloudPass)
                .setBodyParameter("timezone-offset", "1")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        final AlertDialog exceptionMessage = createMessageOnException(e, result, context);
                        if (exceptionMessage != null) {
                            exceptionMessage.show();
                            return;
                        }
                        //TODO check if request token is found
                        final Matcher matcher = REQUEST_TOKEN_PATTERN.matcher(result);
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
                                        Notification.Builder doneNotification = new Notification.Builder(context)
                                                .setAutoCancel(true)
                                                .setSmallIcon(android.R.drawable.stat_sys_upload_done);
                                        final AlertDialog exceptionMessage = createMessageOnException(e, result, context);
                                        if (exceptionMessage != null) {
                                            doneNotification
                                                    .setContentTitle("Error while saving bookmark")
                                                    .setContentText(e.getMessage());
                                            exceptionMessage.show();
                                        } else {
                                            doneNotification
                                                    .setContentTitle("Saving bookmark complete")
                                                    .setContentText("Successfully uploaded bookmark");
                                        }
                                        notificationManager.notify(0, doneNotification.build());
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

    private static String makeUrl(String base, String location) {
        final boolean noSlash = base.endsWith("/") || location.startsWith("/");
        return String.format("%s%s%s",base, noSlash ? "" : "/", location);
    }
}
