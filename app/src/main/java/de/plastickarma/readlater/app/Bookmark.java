package de.plastickarma.readlater.app;

/**
 * Class, that represents a bookmark.
 */
public final class Bookmark {

    private final String url;
    private final String title;
    private final String description;
    private final String categories;


    public Bookmark(
            final String url,
            final String title,
            final String description,
            final String categories) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getCategories() {
        return categories;
    }
}
