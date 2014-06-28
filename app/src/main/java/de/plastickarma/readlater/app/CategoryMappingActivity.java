package de.plastickarma.readlater.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


/**
 * Activity to manage category mappings.
 */
public final class CategoryMappingActivity extends ActionBarActivity implements CategoryMappingHandler {

    public static final String CATEGORY_MAPPING_PREFS = "categoryMappings";
    private Menu menu;
    private boolean showingMappingDetails = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_mapping);

        // Start with showing all mappings
        CategoryMappingsFragment mappingFragment = new CategoryMappingsFragment(this);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, mappingFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.category_mapping, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_add_mapping) {
           showCategoryMappingDetails(null);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a new {@link CategoryMappingDetailFragment} and displays with the given category mapping. The mapping
     * can be <code>null</code>.
     */
    private void showCategoryMappingDetails(final CategoryMapping mapping) {
        final MenuItem item = menu.findItem(R.id.action_add_mapping);
        item.setVisible(false);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final CategoryMappingDetailFragment fragment = new CategoryMappingDetailFragment(this, mapping);
        fragment.setOnSaveHook(new Runnable() {
            @Override
            public void run() {
                item.setVisible(true);
                setShowingMappingDetails(false);

                if (mapping != null) {
                    CategoryMapping.removeCategoryMapping(CategoryMappingActivity.this, mapping);
                }
            }
        });
        fragmentTransaction.replace(R.id.container, fragment);
        this.showingMappingDetails = true;
        fragmentTransaction.commit();
    }

    private void showAllCategories() {
        this.showingMappingDetails = false;
        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new CategoryMappingsFragment(this));
        fragmentTransaction.commit();
    }

    private void setShowingMappingDetails(final boolean value) {
        this.showingMappingDetails = value;
    }

    @Override
    public void onBackPressed() {
        if(this.showingMappingDetails) {
            showAllCategories();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void handleEditMapping(final CategoryMapping mapping) {
        showCategoryMappingDetails(mapping);
    }

    @Override
    public void handleCreateMapping(final CategoryMapping mapping) {
        CategoryMapping.addCategoryMapping(this, mapping);
        showAllCategories();
    }
}
