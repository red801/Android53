package com.example.android53.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android53.R;
import com.example.android53.data.DataRepository;
import com.example.android53.model.Album;
import com.example.android53.model.Photo;
import com.example.android53.model.Tag;
import com.example.android53.model.TagType;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class PhotoViewerFragment extends Fragment {

    private static final String ARG_ALBUM_ID = "album_id";
    private static final String ARG_PHOTO_ID = "photo_id";

    public interface Callbacks {
        void onBackRequested();
    }

    private String albumId;
    private String photoId;
    private DataRepository repository;
    private Album album;
    private int currentIndex = 0;
    private TextView albumNameView;

    private ImageView imageView;
    private TextView captionView;
    private TextView positionView;
    private ChipGroup tagGroup;
    private Button prevButton;
    private Button nextButton;

    private static final String ARG_IS_SEARCH = "is_search";
    private static final String ARG_SEARCH_ALBUM_IDS = "search_album_ids";
    private static final String ARG_SEARCH_PHOTO_IDS = "search_photo_ids";
    private static final String ARG_SEARCH_INDEX = "search_index";
    private boolean isSearchMode = false;
    private ArrayList<String> searchAlbumIds;
    private ArrayList<String> searchPhotoIds;



    public static PhotoViewerFragment newInstance(String albumId, String photoId) {
        PhotoViewerFragment fragment = new PhotoViewerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ALBUM_ID, albumId);
        args.putString(ARG_PHOTO_ID, photoId);
        args.putBoolean(ARG_IS_SEARCH, false); // album mode
        fragment.setArguments(args);
        return fragment;
    }

    public static PhotoViewerFragment newInstanceForSearch(
            ArrayList<String> albumIds,
            ArrayList<String> photoIds,
            int startIndex
    ) {
        PhotoViewerFragment fragment = new PhotoViewerFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_SEARCH, true);
        args.putStringArrayList(ARG_SEARCH_ALBUM_IDS, albumIds);
        args.putStringArrayList(ARG_SEARCH_PHOTO_IDS, photoIds);
        args.putInt(ARG_SEARCH_INDEX, startIndex);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isSearchMode = getArguments().getBoolean(ARG_IS_SEARCH, false);
            if (isSearchMode) {
                searchAlbumIds = getArguments().getStringArrayList(ARG_SEARCH_ALBUM_IDS);
                searchPhotoIds = getArguments().getStringArrayList(ARG_SEARCH_PHOTO_IDS);
                currentIndex = getArguments().getInt(ARG_SEARCH_INDEX, 0);
            } else {
                albumId = getArguments().getString(ARG_ALBUM_ID);
                photoId = getArguments().getString(ARG_PHOTO_ID);
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_viewer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        albumNameView = view.findViewById(R.id.photoAlbumName);
        super.onViewCreated(view, savedInstanceState);
        repository = DataRepository.getInstance(requireContext());
        imageView = view.findViewById(R.id.photoFullImage);
        captionView = view.findViewById(R.id.photoCaption);
        positionView = view.findViewById(R.id.photoPosition);
        tagGroup = view.findViewById(R.id.tagGroup);
        prevButton = view.findViewById(R.id.prevButton);
        nextButton = view.findViewById(R.id.nextButton);
        view.findViewById(R.id.addTagButton).setOnClickListener(v -> promptAddTag());
        prevButton.setOnClickListener(v -> moveTo(currentIndex - 1));
        nextButton.setOnClickListener(v -> moveTo(currentIndex + 1));
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        if (isSearchMode) {
            // In search mode we just bind whatever the currentIndex points to
            bindPhoto();
            return;
        }

        // Album mode (existing behavior)
        album = repository.getAlbumById(albumId);
        if (album == null) {
            Toast.makeText(requireContext(), "Album missing", Toast.LENGTH_SHORT).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return;
        }
        List<Photo> photos = album.getPhotos();
        for (int i = 0; i < photos.size(); i++) {
            if (photos.get(i).getId().equals(photoId)) {
                currentIndex = i;
                break;
            }
        }
        requireActivity().setTitle(album.getName());
        bindPhoto();
    }

    private void bindPhoto() {
        if (isSearchMode) {
            // Search mode: only walk through the matching results
            if (searchPhotoIds == null || searchPhotoIds.isEmpty()) {
                Toast.makeText(requireContext(), "No search results", Toast.LENGTH_SHORT).show();
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
                return;
            }

            if (currentIndex < 0) {
                currentIndex = 0;
            } else if (currentIndex >= searchPhotoIds.size()) {
                currentIndex = searchPhotoIds.size() - 1;
            }

            albumId = searchAlbumIds.get(currentIndex);
            photoId = searchPhotoIds.get(currentIndex);

            Album currentAlbum = repository.getAlbumById(albumId);
            if (currentAlbum == null) {
                Toast.makeText(requireContext(), "Album missing", Toast.LENGTH_SHORT).show();
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
                return;
            }

            // Find the photo inside this album
            Photo photo = null;
            for (Photo p : currentAlbum.getPhotos()) {
                if (p.getId().equals(photoId)) {
                    photo = p;
                    break;
                }
            }
            if (photo == null) {
                Toast.makeText(requireContext(), "Photo missing", Toast.LENGTH_SHORT).show();
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
                return;
            }

            album = currentAlbum; // so promptAddTag can use it

            requireActivity().setTitle("Search results");

            imageView.setImageURI(null);
            imageView.setImageURI(photo.getUri());
            captionView.setText(photo.getCaption());
            positionView.setText((currentIndex + 1) + " / " + searchPhotoIds.size());

            renderTags(photo);
            albumNameView.setText(currentAlbum.getName());

            prevButton.setEnabled(currentIndex > 0);
            nextButton.setEnabled(currentIndex < searchPhotoIds.size() - 1);
            return;
        }

        // -------- Album mode (your old logic) --------
        List<Photo> photos = album.getPhotos();
        if (photos.isEmpty()) {
            Toast.makeText(requireContext(), "No photos in album", Toast.LENGTH_SHORT).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return;
        }
        if (currentIndex < 0) {
            currentIndex = 0;
        } else if (currentIndex >= photos.size()) {
            currentIndex = photos.size() - 1;
        }
        Photo photo = photos.get(currentIndex);
        photoId = photo.getId();
        albumNameView.setText(album.getName());

        requireActivity().setTitle(album.getName());

        imageView.setImageURI(null);
        imageView.setImageURI(photo.getUri());
        captionView.setText(photo.getCaption());
        positionView.setText((currentIndex + 1) + " / " + photos.size());
        renderTags(photo);
        prevButton.setEnabled(currentIndex > 0);
        nextButton.setEnabled(currentIndex < photos.size() - 1);
    }

    private void moveTo(int index) {
        currentIndex = index;
        bindPhoto();
    }

    private void renderTags(Photo photo) {
        tagGroup.removeAllViews();
        for (Tag tag : photo.getTags()) {
            Chip chip = new Chip(requireContext());
            chip.setText(tag.getType().getStorageName() + ": " + tag.getValue());
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                repository.removeTagFromPhoto(albumId, photo.getId(), tag);
                bindPhoto();
            });
            tagGroup.addView(chip);
        }
    }

    private void promptAddTag() {
        if (album == null) {
            return;
        }
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.view_add_tag, null);
        Spinner typeSpinner = dialogView.findViewById(R.id.tagTypeSpinner);
        EditText valueInput = dialogView.findViewById(R.id.tagValueInput);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Person", "Location"});
        typeSpinner.setAdapter(typeAdapter);
        new AlertDialog.Builder(requireContext())
                .setTitle("Add Tag")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String value = valueInput.getText().toString().trim();
                    if (value.isEmpty()) {
                        Toast.makeText(requireContext(), "Enter a value", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    TagType type = typeSpinner.getSelectedItemPosition() == 0 ? TagType.PERSON : TagType.LOCATION;
                    Tag tag = new Tag(type, value);
                    repository.addTagToPhoto(albumId, photoId, tag);
                    bindPhoto();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
