package com.example.android53.model;

public enum TagType {
    PERSON("person"),
    LOCATION("location");

    private final String storageName;

    TagType(String storageName) {
        this.storageName = storageName;
    }

    public String getStorageName() {
        return storageName;
    }

    public static TagType fromStorageName(String value) {
        for (TagType type : values()) {
            if (type.storageName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
