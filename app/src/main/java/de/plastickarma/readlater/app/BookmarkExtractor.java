package de.plastickarma.readlater.app;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BookmarkExtractor {

    private static Pattern URL_REGEX = Pattern.compile("(.*)\\s*(http[s]?://\\S+)\\s*(.*)");

    /**
     * Extracts an URL from the given text. Any preceding text is treated as a title for the bookmark.
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
