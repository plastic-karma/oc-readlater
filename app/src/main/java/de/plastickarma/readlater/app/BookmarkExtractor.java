package de.plastickarma.readlater.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to extract a {@link de.plastickarma.readlater.app.Bookmark} from a text.
 */
public final class BookmarkExtractor {

    private static Pattern URL_REGEX = Pattern.compile("(.*)\\s*(http[s]?://\\S+)\\s*(.*)");

    /**
     * Extracts an URL from the given text. Any preceding text is treated as a title for the bookmark.
     * @return A bookmark from the given text or <code>null</code>, if the text did not contain a url.
     */
    public static Bookmark extractBookmark(String text) {

        Matcher matcher = URL_REGEX.matcher(text);

        if (matcher.matches()) {
            String title = matcher.group(1);
            String url = matcher.group(2);
            return new Bookmark(url, title, "", "");
        }
        return null;
    }
}
