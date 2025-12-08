package com.example.android53.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Photo implements Serializable {
    private final String id;
    private final String uriString;
    private final String caption;
    private final long addedAt;
    private final List<Tag> tags = new ArrayList<>();

    public Photo(Uri uri, String captionText) {
        this(UUID.randomUUID().toString(), uri.toString(), captionText, System.currentTimeMillis());
    }

    public Photo(String id, String uriString, String caption, long addedAt) {
        this.id = id;
        this.uriString = uriString;
        this.caption = caption;
        this.addedAt = addedAt;
    }

    public String getId() {
        return id;
    }

    public Uri getUri() {
        return Uri.parse(uriString);
    }

    public String getFilePath() {
        return uriString;
    }

    public String getCaption() {
        return caption;
    }

    public long getAddedAt() {
        return addedAt;
    }

    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void addTag(Tag tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }
}
