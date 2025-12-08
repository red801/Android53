package com.example.android53.data;

import com.example.android53.model.Album;
import com.example.android53.model.Photo;

public class SearchResult {
    private final Album album;
    private final Photo photo;

    public SearchResult(Album album, Photo photo) {
        this.album = album;
        this.photo = photo;
    }

    public Album getAlbum() {
        return album;
    }

    public Photo getPhoto() {
        return photo;
    }
}
