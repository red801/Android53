package com.example.android53.model;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * This is a string representing the image location.
     * For Android, this should be a URI string (e.g. "content://...", "file:///...")
     */
    private final String filePath;
    private String caption;
    private final LocalDateTime dateTaken;
    private final List<Tag> tags;

    // Tag types that can only have one value
    private static final Set<String> SINGLE_VALUED_TAGS =
            Set.of("location");

    public Photo(String filePath) {
        this.filePath = filePath;
        this.caption = "";
        this.tags = new ArrayList<>();
        this.dateTaken = extractDate(filePath);
    }

    /**
     * Attempt to extract last-modified time ONLY if the path
     * appears to represent a real file on disk.
     *
     * On Android, content URIs do not support FileTime, so this will
     * fall back to LocalDateTime.now().
     */
    private LocalDateTime extractDate(String path) {
        try {
            // If it's a plain path and the file exists
            Path p = Path.of(path);
            if (Files.exists(p)) {
                FileTime ft = Files.getLastModifiedTime(p);
                return LocalDateTime.ofInstant(ft.toInstant(), ZoneId.systemDefault());
            }
        } catch (Exception ignored) {}

        // Android URIs like content:// cannot be resolved here; fallback:
        return LocalDateTime.now();
    }

    // --- Getters & Setters ---

    public String getFilePath() {
        return filePath;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public LocalDateTime getDateTaken() {
        return dateTaken;
    }

    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    // --- Tag operations ---

    public void addTag(Tag tag) {
        if (SINGLE_VALUED_TAGS.contains(tag.getName())) {
            tags.removeIf(t -> t.getName().equalsIgnoreCase(tag.getName()));
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    // --- Equality & Display ---

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Photo)) return false;
        Photo other = (Photo) obj;
        return filePath.equalsIgnoreCase(other.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath.toLowerCase());
    }

    @Override
    public String toString() {
        File f = new File(filePath);
        return caption.isEmpty() ? f.getName() : caption;
    }
}

