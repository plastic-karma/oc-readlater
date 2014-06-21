package de.plastickarma.readlater.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * Fragment to add a category mapping.
 */
public final class AddMappingFragment extends Fragment {

    public AddMappingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        saveMappingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                CategoryMapping.addCategoryMapping(
                        getActivity(),
                        new CategoryMapping(
                            ActivityHelper.getNullCheckedText(bookmarkPattern),
                            ActivityHelper.getNullCheckedText(bookmarkCategories))
                );

                // Close this fragment
                final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new MappingsFragment());
                fragmentTransaction.commit();
            }
        });
        return rootView;
    }
}
