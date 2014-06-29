package de.plastickarma.readlater.app;

import android.content.Context;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates a request to an owncloud instance.
 */
public abstract class OwncloudRequest {

    private static final String ADD_BM_LOCATION =
            "index.php?redirect_url=%2Fowncloud%2Findex.php%2Fapps%2Fbookmarks%2FaddBm.php";


    private static Pattern REQUEST_TOKEN_PATTERN = Pattern.compile("name=\"requesttoken\"\\s*value=\"(.*)\"");


    private final String baseUrl;
    private final String userName;
    private final String password;

    /**
     * Creates a new {@link OwncloudRequest}.
     * @param baseUrl The URL to the owncloud instance
     * @param userName The user name for the owncloud instance
     * @param password The password for the given owncloud user.
     */
    public OwncloudRequest(
        final String baseUrl,
        final String userName,
        final String password) {
        this.baseUrl = baseUrl;
        this.userName = userName;
        this.password = password;
    }

    /**
     * Is called, when the request could be made successfully.
     */
    protected abstract void onSuccess(final Context context, final String result);

    /**
     * Is called, when some error occurred while making request.
     */
    protected abstract void onError(final Context context, Exception e);

    /**
     * Gets called before the execution of the request.
     */
    protected  abstract void onBeforeExecute(final Context context);

    /**
     *  Gets called before the request is made. The given request builder is used
     *  to make the request and thus can be enhanced with request parameters and a like.
     */
    protected void modifyRequest(final Builders.Any.B requestBuilder) {
    }

    /**
     *  Makes a call to the relative location of the owncloud instance, that was specified in this request object.
     */
    public void execute(final Context context, final String location) {
        this.onBeforeExecute(context);
        Ion.with(context, makeUrl(baseUrl, ADD_BM_LOCATION))
                .setBodyParameter("user", this.userName)
                .setBodyParameter("password", this.password)
                .setBodyParameter("password-clone", this.password)
                .setBodyParameter("timezone-offset", "1")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(final Exception e, final String result) {
                        if (e != null) {
                            onError(context, new OwncloudRequestException("Error, while calling owncloud instance" ,e));
                            return;
                        }

                        final Matcher matcher = REQUEST_TOKEN_PATTERN.matcher(result);
                        if (!matcher.find()) {
                            onError(context, new OwncloudRequestException(
                                    "response from owncloud instance could not be interpreted"));
                            return;
                        }
                        final String requestToken = matcher.group(1);
                        final Builders.Any.B requestBuilder = Ion.with(context, makeUrl(baseUrl, location));
                        modifyRequest(requestBuilder);
                        requestBuilder
                                .setBodyParameter("requesttoken", requestToken)
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(final Exception e, final String result) {
                                        if (e != null) {
                                            onError(context, new OwncloudRequestException(
                                                    "Error, while calling owncloud instance" ,e));
                                            return;
                                        }

                                        if (!owncloudResponseIsOk(result)) {
                                            onError(context, new OwncloudRequestException("Owncloud instance signaled an error"));
                                            return;
                                        }
                                        onSuccess(context, result);
                                    }
                                });
                    }
                });
    }

    private static String makeUrl(String base, String location) {
        final boolean noSlash = base.endsWith("/") || location.startsWith("/");
        return String.format("%s%s%s",base, noSlash ? "" : "/", location);
    }

    private static boolean owncloudResponseIsOk(final String httpResult) {
        try {
            JSONObject jsonResult = new JSONObject(httpResult);
            return jsonResult.has("status") && jsonResult.getString("status").equalsIgnoreCase("success");
        } catch (JSONException e) {
            return false;
        }
    }





}
