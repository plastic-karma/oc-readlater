package de.plastickarma.readlater.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter, that displays a category mapping in a ListView.
 */
public final class CategoryMappingListItemAdapter extends BaseAdapter {

    private final List<CategoryMapping> mappings;
    private final Context context;
    private final CategoryMappingHandler categoryMappingHandler;

    /**
     * Creates a new {@link CategoryMappingListItemAdapter}.
     * @param context Context, in which this adapter is created.
     * @param categoryMappingHandler Handler for CRUD-like events of category mappings.
     * @param mappings Category mappings, that shall be displayed.
     */
    public CategoryMappingListItemAdapter(
            final Context context,
            final CategoryMappingHandler categoryMappingHandler,
            final List<CategoryMapping> mappings) {
        this.context = context;
        this.mappings = mappings;
        this.categoryMappingHandler = categoryMappingHandler;
    }

    @Override
    public int getCount() {
        return mappings.size();
    }

    @Override
    public Object getItem(final int position) {
        return mappings.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final CategoryMapping mapping = this.mappings.get(position);
        final View currentView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            currentView = inflater.inflate(R.layout.mapping_row, null);
        } else {
            currentView = convertView;
        }

        TextView text = (TextView) currentView.findViewById(R.id.mapping_row_text);
        text.setText(mapping.getText() + " → " + mapping.getCategories());

        ImageButton removeButton = (ImageButton) currentView.findViewById(R.id.mapping_row_delete_button);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mappings.remove(mapping);
                CategoryMapping.removeCategoryMapping(v.getContext(), mapping);
                notifyDataSetChanged();
            }
        });
        removeButton.setImageResource(android.R.drawable.ic_menu_delete);

        ImageButton editButton = (ImageButton) currentView.findViewById(R.id.mapping_row_edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                categoryMappingHandler.handleEditMapping(mapping);
            }
        });
        editButton.setImageResource(android.R.drawable.ic_menu_edit);

        return currentView;
    }
}
