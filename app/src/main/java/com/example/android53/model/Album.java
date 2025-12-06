package com.example.android53.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a photo album.
 * Contains a collection of photos and provides methods for managing them.
 */
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private final List<Photo> photos;

    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    // --- Name ---

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    // --- Photos ---

    /** Returns an unmodifiable view of the photos in this album. */
    public List<Photo> getPhotos() {
        return Collections.unmodifiableList(photos);
    }

    /** Internal helper for cases where mutation is needed inside the class. */
    private List<Photo> getMutablePhotos() {
        return photos;
    }

    public boolean addPhoto(Photo p) {
        if (photos.contains(p)) {
            return false; // already in this album
        }
        photos.add(p);
        return true;
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    public int getPhotoCount() {
        return photos.size();
    }

    // --- Date helpers ---

    public LocalDate getStartDate() {
        if (photos.isEmpty()) return null;

        LocalDate start = null;
        for (Photo p : photos) {
            LocalDateTime dt = p.getDateTaken();
            if (dt == null) continue;
            LocalDate d = dt.toLocalDate();
            if (start == null || d.isBefore(start)) {
                start = d;
            }
        }
        return start;
    }

    public LocalDate getEndDate() {
        if (photos.isEmpty()) return null;

        LocalDate end = null;
        for (Photo p : photos) {
            LocalDateTime dt = p.getDateTaken();
            if (dt == null) continue;
            LocalDate d = dt.toLocalDate();
            if (end == null || d.isAfter(end)) {
                end = d;
            }
        }
        return end;
    }

    /**
     * Returns a human-readable date range like "2024-01-02 to 2024-03-10",
     * or "No photos"/"No valid dates" if appropriate.
     */
    public String getDateRange() {
        if (photos.isEmpty()) {
            return "No photos";
        }

        List<LocalDateTime> dates = photos.stream()
                .map(Photo::getDateTaken)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (dates.isEmpty()) {
            return "No valid dates";
        }

        LocalDateTime earliest = Collections.min(dates);
        LocalDateTime latest = Collections.max(dates);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return earliest.toLocalDate().format(formatter) + " to " + latest.toLocalDate().format(formatter);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Album)) return false;
        Album other = (Album) obj;
        // If you want case-insensitive album names:
        return name != null && other.name != null
                && name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : name.toLowerCase().hashCode();
    }

}
