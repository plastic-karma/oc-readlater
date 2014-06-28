package de.plastickarma.readlater.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * Fragment to edit details of a  category mapping.
 */
public final class CategoryMappingDetailFragment extends Fragment {

    private final CategoryMappingHandler categoryMappingHandler;
    private final CategoryMapping mapping;
    private Runnable onSaveHook;

    /**
     * Created a new {@link CategoryMappingDetailFragment}.
     * @param categoryMappingHandler Handler to handle category mapping CRUD-like events.
     * @param mapping Category mapping, that shall be displayed. Can be <code>null</code>.
     */
    public CategoryMappingDetailFragment(
            final CategoryMappingHandler categoryMappingHandler,
            final CategoryMapping mapping) {
        this.categoryMappingHandler = categoryMappingHandler;
        this.mapping = mapping;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    public void setOnSaveHook(final Runnable onSaveHook) {
        this.onSaveHook = onSaveHook;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_add_category_mapping, container, false);

        final EditText bookmarkPattern = (EditText) rootView.findViewById(R.id.bookmarkPattern);
        final EditText bookmarkCategories = (EditText) rootView.findViewById(R.id.mappingCategories);


        final Button saveMappingButton = (Button) rootView.findViewById(R.id.saveMappingButton);
        saveMappingButton.setEnabled(false);

        new RequireNonEmptyTextWatcher(bookmarkPattern, bookmarkCategories) {

            @Override
            public void allAreNonEmpty() {
                saveMappingButton.setEnabled(true);
            }

            @Override
            public void someAreEmpty() {
                saveMappingButton.setEnabled(false);
            }
        };

        if (mapping != null) {
            bookmarkCategories.setText(mapping.getCategories());
            bookmarkPattern.setText(mapping.getText());
        }

        saveMappingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (onSaveHook != null) {
                    onSaveHook.run();
                }
                categoryMappingHandler.handleCreateMapping(new CategoryMapping(
                        ActivityHelper.getNullCheckedText(bookmarkPattern),
                        ActivityHelper.getNullCheckedText(bookmarkCategories)));
            }
        });
        return rootView;
    }
}
