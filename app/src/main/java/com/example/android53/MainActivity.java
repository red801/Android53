package com.example.android53;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android53.model.DataStore;
import com.example.android53.ui.AlbumListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load persisted data
        DataStore.getInstance().load(getApplicationContext());

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, AlbumListFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        DataStore.getInstance().save(getApplicationContext());
    }
}
