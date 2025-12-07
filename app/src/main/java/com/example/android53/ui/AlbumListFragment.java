package com.example.android53.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android53.R;
import com.example.android53.model.Album;
import com.example.android53.model.DataStore;

import java.util.List;

public class AlbumListFragment extends Fragment {

    public static AlbumListFragment newInstance() {
        return new AlbumListFragment();
    }

    public AlbumListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler = view.findViewById(R.id.albumRecyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Album> albums = DataStore.getInstance().getAlbums();

        AlbumListAdapter adapter = new AlbumListAdapter(albums, album -> {
            if (getActivity() == null) return;

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, PhotoGridFragment.newInstance(album.getName()))
                    .addToBackStack(null)
                    .commit();
        });


        recycler.setAdapter(adapter);

        // Add album button
        view.findViewById(R.id.addAlbumButton).setOnClickListener(v -> showAddAlbumDialog(adapter));
    }

    private void showAddAlbumDialog(AlbumListAdapter adapter) {
        EditText input = new EditText(getContext());
        new AlertDialog.Builder(getContext())
                .setTitle("New Album")
                .setMessage("Enter album name:")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        Album a = new Album(name);
                        DataStore.getInstance().addAlbum(a);
                        DataStore.getInstance().save(getContext());
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
