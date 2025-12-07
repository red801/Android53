package com.example.android53.model;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataStore implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String FILE_NAME = "albums.ser";

    // Singleton instance
    private static DataStore instance;

    // All albums in the app
    private ArrayList<Album> albums = new ArrayList<>();

    private DataStore() { }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // --- Accessors ---

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }

    // Optional convenience methods
    public void addAlbum(Album album) {
        if (!albums.contains(album)) {
            albums.add(album);
        }
    }

    public Album getAlbumByName(String name) {
        if (name == null) return null;
        for (Album a : albums) {
            if (a.getName() != null && a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }


    public void removeAlbum(Album album) {
        albums.remove(album);
    }

    // --- Persistence ---

    /**
     * Load DataStore from internal storage.
     * Call this once in MainActivity.onCreate().
     */
    public void load(Context context) {
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            DataStore loaded = (DataStore) ois.readObject();
            // Replace the singleton with the loaded object
            instance = loaded;

        } catch (FileNotFoundException e) {
            // First run: no file yet -> keep empty albums list
            albums = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            albums = new ArrayList<>();
        }
    }

    /**
     * Save current DataStore to internal storage.
     * Call this after any change and/or in MainActivity.onStop().
     */
    public void save(Context context) {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
