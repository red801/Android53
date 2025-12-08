package com.example.android53.model;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

public class Tag implements Serializable {
    private final TagType type;
    private final String value;

    public Tag(TagType type, String value) {
        this.type = type;
        this.value = value == null ? "" : value.trim();
    }

    public TagType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean matches(Tag other) {
        if (other == null || other.type == null || type == null) {
            return false;
        }
        return type == other.type && valueEquals(other.value);
    }

    private boolean valueEquals(String otherValue) {
        return value.equalsIgnoreCase(otherValue == null ? "" : otherValue.trim());
    }

    public boolean valueStartsWith(String prefix) {
        if (prefix == null) {
            return false;
        }
        return value.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return type == tag.type && valueEquals(tag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value.toLowerCase(Locale.ROOT));
    }
}
