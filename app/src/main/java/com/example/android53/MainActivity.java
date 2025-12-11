package com.example.android53;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.android53.ui.AlbumListFragment;
import com.example.android53.ui.PhotoGridFragment;
import com.example.android53.ui.PhotoViewerFragment;
import com.example.android53.ui.SearchFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        AlbumListFragment.Callbacks,
        PhotoGridFragment.Callbacks,
        PhotoViewerFragment.Callbacks,
        SearchFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            navigateTo(new AlbumListFragment(), false);
        }
    }

    private void navigateTo(Fragment fragment, boolean addToBackStack) {
        var transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
    @Override
    public void onSearchResultSelected(ArrayList<String> albumIds,
                                       ArrayList<String> photoIds,
                                       int startIndex) {
        navigateTo(
                PhotoViewerFragment.newInstanceForSearch(albumIds, photoIds, startIndex),
                true
        );
    }


    @Override
    public void onAlbumSelected(String albumId) {
        navigateTo(PhotoGridFragment.newInstance(albumId), true);
    }

    @Override
    public void onShowSearch() {
        navigateTo(new SearchFragment(), true);
    }

    @Override
    public void onPhotoSelected(String albumId, String photoId) {
        navigateTo(PhotoViewerFragment.newInstance(albumId, photoId), true);
    }

    @Override
    public void onBackRequested() {
        getSupportFragmentManager().popBackStack();
    }
}
