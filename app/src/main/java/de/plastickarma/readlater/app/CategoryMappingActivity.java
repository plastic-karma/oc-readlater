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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_mapping, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_add_mapping) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            final CategoryMappingDetailFragment fragment = new CategoryMappingDetailFragment(this, null);
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleEditMapping(final CategoryMapping mapping) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final CategoryMappingDetailFragment fragment = new CategoryMappingDetailFragment(this, mapping);
        fragment.setOnSaveHook(new Runnable() {
            @Override
            public void run() {
                CategoryMapping.removeCategoryMapping(CategoryMappingActivity.this, mapping);
            }
        });
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void handleCreateMapping(final CategoryMapping mapping) {
        CategoryMapping.addCategoryMapping(this, mapping);

        final FragmentManager fragmentManager = this.getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new CategoryMappingsFragment(this));
        fragmentTransaction.commit();
    }
}
