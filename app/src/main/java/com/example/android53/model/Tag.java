package com.example.android53.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a tag assigned to a photo.
 * Each tag has a name (like "location") and a value (like "Paris").
 */
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private String value;

    /**
     * Creates a Tag with a name and value.
     * @param name tag type (e.g. "location", "person")
     * @param value tag value (e.g. "paris", "alice")
     */
    public Tag(String name, String value) {
        this.name = safe(name);
        this.value = safe(value);
    }

    // Normalize input: null → "", lowercase, trim
    private static String safe(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    // Getters
    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    // Setter for value only (name is immutable)
    public void setValue(String value) {
        this.value = safe(value);
    }

    // Equality is case-insensitive for both name and value
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Tag)) return false;
        Tag other = (Tag) obj;
        return name.equalsIgnoreCase(other.name) &&
                value.equalsIgnoreCase(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), value.toLowerCase());
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }
}
