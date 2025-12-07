package com.example.android53.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android53.R;
import com.example.android53.data.DataRepository;
import com.example.android53.model.Album;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AlbumListFragment extends Fragment implements AlbumAdapter.Listener {

    public interface Callbacks {
        void onAlbumSelected(String albumId);

        void onShowSearch();
    }

    private DataRepository repository;
    private AlbumAdapter adapter;
    private Callbacks callbacks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_album_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callbacks = (Callbacks) requireActivity();
        repository = DataRepository.getInstance(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.albumRecyclerView);
        adapter = new AlbumAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        FloatingActionButton addAlbumButton = view.findViewById(R.id.addAlbumButton);
        addAlbumButton.setOnClickListener(v -> promptForAlbum(null));
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
        requireActivity().setTitle("Albums");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_album_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            callbacks.onShowSearch();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reload() {
        adapter.submit(new ArrayList<>(repository.getAlbums()));
    }

    private void promptForAlbum(@Nullable Album existing) {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (existing != null) {
            input.setText(existing.getName());
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(existing == null ? "New Album" : "Rename Album")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    boolean success;
                    if (existing == null) {
                        success = repository.createAlbum(name) != null;
                    } else {
                        success = repository.renameAlbum(existing.getId(), name);
                    }
                    if (!success) {
                        Toast.makeText(requireContext(), "Album name must be unique", Toast.LENGTH_SHORT).show();
                    }
                    reload();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(Album album) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete album?")
                .setMessage("This will remove \"" + album.getName() + "\" and its photos.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    repository.deleteAlbum(album.getId());
                    reload();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onAlbumClick(Album album) {
        callbacks.onAlbumSelected(album.getId());
    }

    @Override
    public void onAlbumLongClick(Album album) {
        String[] options = new String[]{"Rename", "Delete"};
        new AlertDialog.Builder(requireContext())
                .setTitle(album.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        promptForAlbum(album);
                    } else if (which == 1) {
                        confirmDelete(album);
                    }
                })
                .show();
    }
}
