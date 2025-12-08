package com.example.android53.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android53.R;
import com.example.android53.data.DataRepository;
import com.example.android53.model.Album;
import com.example.android53.model.Photo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PhotoGridFragment extends Fragment implements PhotoAdapter.Listener {

    private static final String ARG_ALBUM_ID = "album_id";

    public interface Callbacks {
        void onPhotoSelected(String albumId, String photoId);
    }

    private String albumId;
    private DataRepository repository;
    private Album album;
    private PhotoAdapter adapter;
    private Callbacks callbacks;
    private ActivityResultLauncher<String[]> photoPickerLauncher;

    public static PhotoGridFragment newInstance(String albumId) {
        PhotoGridFragment fragment = new PhotoGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ALBUM_ID, albumId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumId = getArguments().getString(ARG_ALBUM_ID);
        }
        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        requireContext().getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        repository.addPhoto(albumId, uri, null);
                        reload();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callbacks = (Callbacks) requireActivity();
        repository = DataRepository.getInstance(requireContext());
        adapter = new PhotoAdapter(this);
        RecyclerView recyclerView = view.findViewById(R.id.photoRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        recyclerView.setAdapter(adapter);
        FloatingActionButton addPhotoButton = view.findViewById(R.id.addPhotoButton);
        addPhotoButton.setOnClickListener(v -> openPicker());
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private void openPicker() {
        photoPickerLauncher.launch(new String[]{"image/*"});
    }

    private void reload() {
        album = repository.getAlbumById(albumId);
        if (album == null) {
            Toast.makeText(requireContext(), "Album not found", Toast.LENGTH_SHORT).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return;
        }
        requireActivity().setTitle(album.getName());
        adapter.submit(new ArrayList<>(album.getPhotos()));
    }

    @Override
    public void onPhotoClick(Photo photo) {
        callbacks.onPhotoSelected(albumId, photo.getId());
    }

    @Override
    public void onPhotoLongClick(Photo photo) {
        String[] options = new String[]{"Remove", "Move"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Photo options")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        repository.removePhoto(albumId, photo.getId());
                        reload();
                    } else if (which == 1) {
                        promptMove(photo);
                    }
                })
                .show();
    }

    private void promptMove(Photo photo) {
        List<Album> albums = repository.getAlbums();
        List<Album> destinations = new ArrayList<>();
        for (Album item : albums) {
            if (!item.getId().equals(albumId)) {
                destinations.add(item);
            }
        }
        if (destinations.isEmpty()) {
            Toast.makeText(requireContext(), "Create another album to move photos", Toast.LENGTH_SHORT).show();
            return;
        }
        CharSequence[] names = new CharSequence[destinations.size()];
        for (int i = 0; i < destinations.size(); i++) {
            names[i] = destinations.get(i).getName();
        }
        new AlertDialog.Builder(requireContext())
                .setTitle("Move to album")
                .setItems(names, (dialog, which) -> {
                    Album target = destinations.get(which);
                    repository.movePhoto(albumId, photo.getId(), target.getId());
                    reload();
                })
                .show();
    }
}
