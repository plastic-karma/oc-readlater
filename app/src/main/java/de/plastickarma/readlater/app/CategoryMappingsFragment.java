package de.plastickarma.readlater.app;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * Fragment to see all category mappings.
 */
public final class CategoryMappingsFragment extends ListFragment {

    private final CategoryMappingHandler categoryMappingHandler;

    /**
     * Creates a new {@link CategoryMappingsFragment}.
     * @param categoryMappingHandler Handler for CRUD-like events of category mappings.
     */
    public CategoryMappingsFragment(final CategoryMappingHandler categoryMappingHandler) {
        this.categoryMappingHandler = categoryMappingHandler;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new CategoryMappingListItemAdapter(
                this.getActivity(),
                this.categoryMappingHandler,
                CategoryMapping.getCategoryMappings(this.getActivity()))
        );
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_mapping, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }
}
