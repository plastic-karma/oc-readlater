package de.plastickarma.readlater.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a mapping between text and categories.
 */
public final class CategoryMapping {

    private final String text;

    private final String categories;


    public CategoryMapping(final String text, final String categories) {
        this.text = text;
        this.categories = categories;
    }

    public String getCategories() {
        return categories;
    }

    public String getText() {
        return text;
    }

    private static List<CategoryMapping> fromJson(final String json) throws JSONException {
        List<CategoryMapping> mappings = new ArrayList<CategoryMapping>();
        final JSONArray fromJSON = new JSONArray(json);
        for (int i = 0; i < fromJSON.length(); i++) {
            final JSONObject jsonCategoryMapping = fromJSON.getJSONObject(i);
            mappings.add(
                    new CategoryMapping(
                            jsonCategoryMapping.getString("text"),
                            jsonCategoryMapping.getString("categories")
                    )
            );
        }
        return mappings;
    }

    private static String toJSON(List<CategoryMapping> categories) throws JSONException {
        JSONArray result = new JSONArray();
        for(CategoryMapping mapping : categories) {
            JSONObject jsonMapping = new JSONObject();
            jsonMapping
                    .put("text", mapping.getText())
                    .put("categories", mapping.getCategories());
            result.put(jsonMapping);
        }
        return result.toString();
    }

    /**
     * Adds the given mapping to the other persisted mappings.
     */
    public static void addCategoryMapping(Context ctx, CategoryMapping mapping) {
        final List<CategoryMapping> mappings = getCategoryMappings(ctx);
        mappings.add(mapping);
        saveCategoryMappings(ctx, mappings);
    }

    /**
     * Removes the given mapping from the other persisted mappings.
     */
    public static void removeCategoryMapping(Context ctx, CategoryMapping mapping) {
        final List<CategoryMapping> mappings = getCategoryMappings(ctx);
        mappings.remove(mapping);
        saveCategoryMappings(ctx, mappings);
    }

    private static void saveCategoryMappings(final Context ctx, final List<CategoryMapping> mappings) {
        final SharedPreferences sharedPreferences =
                ctx.getSharedPreferences(Settings.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEdit = sharedPreferences.edit();
        final String json;
        try {
            json = CategoryMapping.toJSON(mappings);
        } catch (JSONException e) {
            Log.e("Categories", "Could not create json", e);
            throw new RuntimeException(e);
        }
        sharedPrefEdit.putString(CategoryMappingActivity.CATEGORY_MAPPING_PREFS, json);
        sharedPrefEdit.commit();
    }

    /**
     * Returns all category mappings from the shared preferences.
     */
    public static List<CategoryMapping> getCategoryMappings(Context ctx) {
        List<CategoryMapping> mappings = new ArrayList<CategoryMapping>();

        final SharedPreferences sharedPreferences =
                ctx.getSharedPreferences(Settings.SHARED_PREF_KEY, Context.MODE_PRIVATE);

        final String categoryMappingsRaw = sharedPreferences.getString(CategoryMappingActivity.CATEGORY_MAPPING_PREFS, null);
        if (categoryMappingsRaw != null) {
            try {
                mappings.addAll(CategoryMapping.fromJson(categoryMappingsRaw));
            } catch (JSONException e) {
                Log.e("Categories", "Error while parsing categories: " + categoryMappingsRaw, e);
                throw new RuntimeException(e);
            }
        }
        return mappings;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof CategoryMapping) {
            CategoryMapping other = (CategoryMapping) o;
            return this.getText().equals(other.getText()) && this.getCategories().equals(other.getCategories());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.text.hashCode() ^ this.categories.hashCode();
    }

    @Override
    public String toString() {
        return this.text + " -> " + this.categories;
    }
}
