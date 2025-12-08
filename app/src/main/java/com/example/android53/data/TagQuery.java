package com.example.android53.data;

import com.example.android53.model.Photo;
import com.example.android53.model.Tag;
import com.example.android53.model.TagType;

import java.util.Locale;

public class TagQuery {
    public static class TagFilter {
        public final TagType type;
        public final String value;

        public TagFilter(TagType type, String value) {
            this.type = type;
            this.value = value == null ? "" : value.trim();
        }

        public boolean matches(Photo photo) {
            if (photo == null || type == null || value.isEmpty()) {
                return false;
            }
            for (Tag tag : photo.getTags()) {
                if (tag.getType() == type && tag.getValue().equalsIgnoreCase(value)) {
                    return true;
                }
            }
            return false;
        }
    }

    private final TagFilter first;
    private final TagFilter second;
    private final boolean useAnd;

    public TagQuery(TagFilter first) {
        this(first, null, true);
    }

    public TagQuery(TagFilter first, TagFilter second, boolean useAnd) {
        this.first = first;
        this.second = second;
        this.useAnd = useAnd;
    }

    public boolean isEmpty() {
        return first == null;
    }

    public boolean matches(Photo photo) {
        if (isEmpty()) {
            return false;
        }
        boolean firstMatch = first.matches(photo);
        if (second == null || second.value.isEmpty()) {
            return firstMatch;
        }
        boolean secondMatch = second.matches(photo);
        return useAnd ? firstMatch && secondMatch : firstMatch || secondMatch;
    }
}
