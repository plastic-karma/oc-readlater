package de.plastickarma.readlater.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;


public final class CategoryMappingAdapter extends BaseAdapter implements View.OnClickListener {

    private final List<CategoryMapping> mappings;
    private final Context context;

    public CategoryMappingAdapter(final Context context, List<CategoryMapping> mappings) {
        this.context = context;
        this.mappings = mappings;
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
        CategoryMapping mapping = this.mappings.get(position);
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
        removeButton.setFocusableInTouchMode(false);
        removeButton.setFocusable(false);
        removeButton.setOnClickListener(this);
        removeButton.setImageResource(android.R.drawable.ic_menu_delete);
        removeButton.setTag(mapping);

        return currentView;
    }

    @Override
    public void onClick(final View v) {
        CategoryMapping mapping = (CategoryMapping) v.getTag();
        mappings.remove(mapping);
        CategoryMapping.removeCategoryMapping(v.getContext(), mapping);
        notifyDataSetChanged();

    }
}
