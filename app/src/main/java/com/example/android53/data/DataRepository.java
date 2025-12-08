package com.example.android53.data;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.example.android53.model.Album;
import com.example.android53.model.Photo;
import com.example.android53.model.Tag;
import com.example.android53.model.TagType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class DataRepository {
    private static final String FILE_NAME = "photos_data.json";
    private static DataRepository instance;

    private final List<Album> albums = new ArrayList<>();
    private final Context appContext;

    private DataRepository(Context context) {
        this.appContext = context.getApplicationContext();
        loadFromDisk();
    }

    public static synchronized DataRepository getInstance(Context context) {
        if (instance == null) {
            instance = new DataRepository(context);
        }
        return instance;
    }

    public List<Album> getAlbums() {
        return Collections.unmodifiableList(albums);
    }

    public Album getAlbumById(String albumId) {
        for (Album album : albums) {
            if (album.getId().equals(albumId)) {
                return album;
            }
        }
        return null;
    }

    public Album createAlbum(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty() || albumNameExists(trimmed)) {
            return null;
        }
        Album album = new Album(trimmed);
        albums.add(album);
        saveToDisk();
        return album;
    }

    public boolean renameAlbum(String albumId, String newName) {
        String trimmed = newName == null ? "" : newName.trim();
        if (trimmed.isEmpty() || albumNameExists(trimmed)) {
            return false;
        }
        Album album = getAlbumById(albumId);
        if (album == null) {
            return false;
        }
        album.setName(trimmed);
        saveToDisk();
        return true;
    }

    public boolean deleteAlbum(String albumId) {
        Album album = getAlbumById(albumId);
        if (album == null) {
            return false;
        }
        albums.remove(album);
        saveToDisk();
        return true;
    }

    public Photo addPhoto(String albumId, Uri uri, String caption) {
        Album album = getAlbumById(albumId);
        if (album == null) {
            return null;
        }
        Photo photo = new Photo(uri, captionFrom(caption, uri));
        album.addPhoto(photo);
        saveToDisk();
        return photo;
    }

    public boolean removePhoto(String albumId, String photoId) {
        Album album = getAlbumById(albumId);
        if (album == null) {
            return false;
        }
        Photo photo = album.findPhoto(photoId);
        if (photo == null) {
            return false;
        }
        album.removePhoto(photo);
        saveToDisk();
        return true;
    }

    public boolean movePhoto(String sourceAlbumId, String photoId, String targetAlbumId) {
        if (sourceAlbumId.equals(targetAlbumId)) {
            return false;
        }
        Album source = getAlbumById(sourceAlbumId);
        Album target = getAlbumById(targetAlbumId);
        if (source == null || target == null) {
            return false;
        }
        Photo photo = source.findPhoto(photoId);
        if (photo == null) {
            return false;
        }
        source.removePhoto(photo);
        target.addPhoto(photo);
        saveToDisk();
        return true;
    }

    public boolean addTagToPhoto(String albumId, String photoId, Tag tag) {
        Album album = getAlbumById(albumId);
        if (album == null || tag == null) {
            return false;
        }
        Photo photo = album.findPhoto(photoId);
        if (photo == null) {
            return false;
        }
        photo.addTag(tag);
        saveToDisk();
        return true;
    }

    public boolean removeTagFromPhoto(String albumId, String photoId, Tag tag) {
        Album album = getAlbumById(albumId);
        if (album == null || tag == null) {
            return false;
        }
        Photo photo = album.findPhoto(photoId);
        if (photo == null) {
            return false;
        }
        photo.removeTag(tag);
        saveToDisk();
        return true;
    }

    public List<SearchResult> search(TagQuery query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        List<SearchResult> results = new ArrayList<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                if (query.matches(photo)) {
                    results.add(new SearchResult(album, photo));
                }
            }
        }
        return results;
    }

    public List<String> autocomplete(TagType type, String prefix) {
        List<String> matches = new ArrayList<>();
        if (type == null || prefix == null) {
            return matches;
        }
        String lowered = prefix.trim().toLowerCase(Locale.ROOT);
        Set<String> seen = new HashSet<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                for (Tag tag : photo.getTags()) {
                    if (tag.getType() == type && tag.valueStartsWith(lowered)) {
                        String value = tag.getValue();
                        if (seen.add(value.toLowerCase(Locale.ROOT))) {
                            matches.add(value);
                        }
                    }
                }
            }
        }
        return matches;
    }

    private boolean albumNameExists(String name) {
        String candidate = name.trim().toLowerCase(Locale.ROOT);
        for (Album album : albums) {
            if (album.getName().toLowerCase(Locale.ROOT).equals(candidate)) {
                return true;
            }
        }
        return false;
    }

    private void loadFromDisk() {
        File file = new File(appContext.getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            parseJson(builder.toString());
        } catch (IOException | JSONException e) {
            // Start clean if parsing fails
            albums.clear();
        }
    }

    private void parseJson(String json) throws JSONException {
        albums.clear();
        JSONObject root = new JSONObject(json);
        JSONArray albumArray = root.optJSONArray("albums");
        if (albumArray == null) {
            return;
        }
        for (int i = 0; i < albumArray.length(); i++) {
            JSONObject albumObj = albumArray.getJSONObject(i);
            String albumId = albumObj.getString("id");
            String name = albumObj.getString("name");
            Album album = new Album(albumId, name);
            JSONArray photos = albumObj.optJSONArray("photos");
            if (photos != null) {
                for (int p = 0; p < photos.length(); p++) {
                    JSONObject photoObj = photos.getJSONObject(p);
                    String photoId = photoObj.optString("id", UUID.randomUUID().toString());
                    String uri = photoObj.getString("uri");
                    String caption = photoObj.optString("caption", "Photo");
                    long addedAt = photoObj.optLong("addedAt", System.currentTimeMillis());
                    Photo photo = new Photo(photoId, uri, caption, addedAt);
                    JSONArray tags = photoObj.optJSONArray("tags");
                    if (tags != null) {
                        for (int t = 0; t < tags.length(); t++) {
                            JSONObject tagObj = tags.getJSONObject(t);
                            TagType type = TagType.fromStorageName(tagObj.optString("type"));
                            String value = tagObj.optString("value", "");
                            if (type != null && !TextUtils.isEmpty(value)) {
                                photo.addTag(new Tag(type, value));
                            }
                        }
                    }
                    album.addPhoto(photo);
                }
            }
            albums.add(album);
        }
    }

    private void saveToDisk() {
        File file = new File(appContext.getFilesDir(), FILE_NAME);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(toJson().toString());
        } catch (IOException | JSONException e) {
            // Ignore write failures in this simple persistence layer
        }
    }

    private JSONObject toJson() throws JSONException {
        JSONObject root = new JSONObject();
        JSONArray albumArray = new JSONArray();
        for (Album album : albums) {
            JSONObject albumObj = new JSONObject();
            albumObj.put("id", album.getId());
            albumObj.put("name", album.getName());
            JSONArray photoArray = new JSONArray();
            for (Photo photo : album.getPhotos()) {
                JSONObject photoObj = new JSONObject();
                photoObj.put("id", photo.getId());
                photoObj.put("uri", photo.getUri().toString());
                photoObj.put("caption", photo.getCaption());
                photoObj.put("addedAt", photo.getAddedAt());
                JSONArray tagArray = new JSONArray();
                for (Tag tag : photo.getTags()) {
                    JSONObject tagObj = new JSONObject();
                    tagObj.put("type", tag.getType().getStorageName());
                    tagObj.put("value", tag.getValue());
                    tagArray.put(tagObj);
                }
                photoObj.put("tags", tagArray);
                photoArray.put(photoObj);
            }
            albumObj.put("photos", photoArray);
            albumArray.put(albumObj);
        }
        root.put("albums", albumArray);
        return root;
    }

    private String captionFrom(String caption, Uri uri) {
        if (!TextUtils.isEmpty(caption)) {
            return caption.trim();
        }
        String last = uri.getLastPathSegment();
        if (last == null) {
            return "Photo";
        }
        int slash = last.lastIndexOf('/');
        return slash >= 0 ? last.substring(slash + 1) : last;
    }
}
