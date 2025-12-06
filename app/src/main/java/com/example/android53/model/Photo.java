package com.example.android53.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * A string representing the image URI.
     * Example: "content://...", "file:///..."
     */
    private final String filePath;

    private String caption;
    private final LocalDateTime dateTaken;
    private final List<Tag> tags;

    /**
     * Tag types that can only have one value.
     * You may expand this list if needed.
     */
    private static final Set<String> SINGLE_VALUED_TAGS =
            Set.of("location");

    /**
     * Constructor.
     * For Android, filePath should usually be a content URI returned
     * by ACTION_OPEN_DOCUMENT.
     */
    public Photo(String filePath) {
        this.filePath = filePath;
        this.caption = "";
        this.tags = new ArrayList<>();

        // Android-safe default date assignment.
        this.dateTaken = LocalDateTime.now();
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
        // Enforce single-valued tags
        if (SINGLE_VALUED_TAGS.contains(tag.getName().toLowerCase())) {
            tags.removeIf(t -> t.getName().equalsIgnoreCase(tag.getName()));
        }

        // Avoid duplicates
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    // --- Equality & Hashing ---

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
        // For debugging or simple display
        return caption.isEmpty() ? filePath : caption;
    }
}
