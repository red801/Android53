// DataStore.java
package com.example.android53.model;

import android.content.Context;

import java.io.*;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

public class DataStore implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String FILE_NAME = "albums.ser";

    private static DataStore instance;

    private ArrayList<Album> albums = new ArrayList<>();

    private DataStore() {}

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }

    // Call this in your main Activity onCreate()
    public void load(Context context) {
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            DataStore loaded = (DataStore) ois.readObject();
            this.albums = loaded.albums;

        } catch (FileNotFoundException e) {
            // First run: no file yet -> start with empty list
            albums = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            albums = new ArrayList<>();
        }
    }

    // Call this whenever you want to persist (e.g. onPause / onStop / after edits)
    public void save(Context context) {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
