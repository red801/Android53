package com.example.android53.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android53.R;
import com.example.android53.model.Album;
import com.example.android53.model.DataStore;
import com.example.android53.model.Photo;

public class PhotoGridFragment extends Fragment {

    private static final String ARG_ALBUM_NAME = "album_name";

    private Album album;
    private PhotoGridAdapter adapter;

    private ActivityResultLauncher<String[]> pickImageLauncher;

    public PhotoGridFragment() { }

    public static PhotoGridFragment newInstance(String albumName) {
        PhotoGridFragment fragment = new PhotoGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ALBUM_NAME, albumName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String albumName = null;
        if (getArguments() != null) {
            albumName = getArguments().getString(ARG_ALBUM_NAME);
        }
        album = DataStore.getInstance().getAlbumByName(albumName);

        // Register the image picker (Storage Access Framework)
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null && album != null) {
                        // (we're skipping persistable permission to keep it simple)
                        Photo p = new Photo(uri.toString());
                        if (album.addPhoto(p)) {
                            // Persist changes
                            if (getContext() != null) {
                                DataStore.getInstance().save(getContext());
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler = view.findViewById(R.id.photoRecyclerView);
        recycler.setLayoutManager(new GridLayoutManager(getContext(), 3));

        if (album != null) {
            adapter = new PhotoGridAdapter(album.getPhotos(), photo -> {
                // TODO: later: open PhotoDetailFragment
            });
            recycler.setAdapter(adapter);
        }

        view.findViewById(R.id.addPhotoButton).setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        String[] mimeTypes = new String[] { "image/*" };
        pickImageLauncher.launch(mimeTypes);
    }
}
