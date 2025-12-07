package com.example.android53.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android53.R;
import com.example.android53.model.Album;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    interface Listener {
        void onAlbumClick(Album album);

        void onAlbumLongClick(Album album);
    }

    private final Listener listener;
    private final List<Album> albums = new ArrayList<>();

    AlbumAdapter(Listener listener) {
        this.listener = listener;
    }

    void submit(List<Album> items) {
        albums.clear();
        albums.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.name.setText(album.getName());
        int count = album.getPhotos().size();
        holder.range.setText(String.format(Locale.getDefault(), "%d photos", count));
        holder.itemView.setOnClickListener(v -> listener.onAlbumClick(album));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onAlbumLongClick(album);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView range;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.albumName);
            range = itemView.findViewById(R.id.dateRange);
        }
    }
}
