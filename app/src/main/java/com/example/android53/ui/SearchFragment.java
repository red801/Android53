package com.example.android53.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android53.R;
import com.example.android53.data.DataRepository;
import com.example.android53.data.SearchResult;
import com.example.android53.data.TagQuery;
import com.example.android53.model.TagType;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchResultAdapter.Listener {

    public interface Callbacks {
        void onSearchResultSelected(ArrayList<String> albumIds,
                                    ArrayList<String> photoIds,
                                    int startIndex);
    }

    private Spinner typeSpinner1;
    private Spinner typeSpinner2;
    private AutoCompleteTextView valueInput1;
    private AutoCompleteTextView valueInput2;
    private RadioGroup operatorGroup;
    private TextView statusText;
    private SearchResultAdapter adapter;
    private DataRepository repository;
    private Callbacks callbacks;
    private List<SearchResult> lastResults = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle("Search");
        callbacks = (Callbacks) requireActivity();
        repository = DataRepository.getInstance(requireContext());
        typeSpinner1 = view.findViewById(R.id.typeSpinner1);
        typeSpinner2 = view.findViewById(R.id.typeSpinner2);
        valueInput1 = view.findViewById(R.id.valueInput1);
        valueInput2 = view.findViewById(R.id.valueInput2);
        operatorGroup = view.findViewById(R.id.operatorGroup);
        statusText = view.findViewById(R.id.searchStatus);
        RecyclerView recyclerView = view.findViewById(R.id.searchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SearchResultAdapter(this);
        recyclerView.setAdapter(adapter);
        setupTypeSpinners();
        attachAutocomplete(valueInput1, typeSpinner1);
        attachAutocomplete(valueInput2, typeSpinner2);
        Button searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> performSearch());
    }

    private void setupTypeSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Person", "Location"});
        typeSpinner1.setAdapter(adapter);
        typeSpinner2.setAdapter(adapter);
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent == typeSpinner1) {
                    updateSuggestions(valueInput1, typeSpinner1);
                } else if (parent == typeSpinner2) {
                    updateSuggestions(valueInput2, typeSpinner2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        typeSpinner1.setOnItemSelectedListener(listener);
        typeSpinner2.setOnItemSelectedListener(listener);
    }

    private void attachAutocomplete(AutoCompleteTextView input, Spinner spinner) {
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSuggestions(input, spinner);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void updateSuggestions(AutoCompleteTextView input, Spinner spinner) {
        TagType type = spinner.getSelectedItemPosition() == 0 ? TagType.PERSON : TagType.LOCATION;
        String prefix = input.getText().toString();
        List<String> values = repository.autocomplete(type, prefix);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                values);
        input.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        input.showDropDown();
    }

    private void performSearch() {
        String value1 = valueInput1.getText().toString().trim();
        if (value1.isEmpty()) {
            Toast.makeText(requireContext(), "Enter at least one tag value", Toast.LENGTH_SHORT).show();
            return;
        }
        TagType type1 = typeSpinner1.getSelectedItemPosition() == 0 ? TagType.PERSON : TagType.LOCATION;
        TagQuery.TagFilter filter1 = new TagQuery.TagFilter(type1, value1);
        String value2 = valueInput2.getText().toString().trim();
        TagQuery query;
        if (value2.isEmpty()) {
            query = new TagQuery(filter1);
        } else {
            TagType type2 = typeSpinner2.getSelectedItemPosition() == 0 ? TagType.PERSON : TagType.LOCATION;
            TagQuery.TagFilter filter2 = new TagQuery.TagFilter(type2, value2);
            boolean useAnd = operatorGroup.getCheckedRadioButtonId() == R.id.andOption;
            query = new TagQuery(filter1, filter2, useAnd);
        }

        List<SearchResult> results = repository.search(query);
        lastResults = results; // <--- keep them

        adapter.submit(new ArrayList<>(results));
        if (results.isEmpty()) {
            statusText.setText("No matches");
        } else {
            statusText.setText("Found " + results.size() + " photos");
        }
    }


    @Override
    public void onResultClick(SearchResult result) {
        if (lastResults == null || lastResults.isEmpty()) {
            return;
        }

        // Build parallel lists of albumIds and photoIds for navigation
        ArrayList<String> albumIds = new ArrayList<>();
        ArrayList<String> photoIds = new ArrayList<>();
        for (SearchResult r : lastResults) {
            albumIds.add(r.getAlbum().getId());
            photoIds.add(r.getPhoto().getId());
        }

        // Find index of the clicked result
        int startIndex = 0;
        for (int i = 0; i < lastResults.size(); i++) {
            SearchResult r = lastResults.get(i);
            if (r.getAlbum().getId().equals(result.getAlbum().getId())
                    && r.getPhoto().getId().equals(result.getPhoto().getId())) {
                startIndex = i;
                break;
            }
        }

        callbacks.onSearchResultSelected(albumIds, photoIds, startIndex);
    }

}
