package com.example.android53.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Album implements Serializable {
    private final String id;
    private String name;
    private final List<Photo> photos = new ArrayList<>();

    public Album(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    public Album(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Photo> getPhotos() {
        return Collections.unmodifiableList(photos);
    }

    public boolean addPhoto(Photo photo) {
        if (photo == null) return false;
        if (photos.contains(photo)) {
            return false; // duplicate
        }
        photos.add(photo);
        return true;
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    public Photo findPhoto(String photoId) {
        for (Photo photo : photos) {
            if (photo.getId().equals(photoId)) {
                return photo;
            }
        }
        return null;
    }

    public String getDateRange() {
        if (photos.isEmpty()) {
            return "No photos";
        }
        long min = Long.MAX_VALUE;
        long max = 0L;
        for (Photo photo : photos) {
            long ts = photo.getAddedAt();
            if (ts < min) {
                min = ts;
            }
            if (ts > max) {
                max = ts;
            }
        }
        if (min == Long.MAX_VALUE) {
            return "No photos";
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        if (min == max) {
            return fmt.format(new Date(min));
        }
        return fmt.format(new Date(min)) + " - " + fmt.format(new Date(max));
    }
}
