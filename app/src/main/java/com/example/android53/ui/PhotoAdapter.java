package com.example.android53.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android53.R;
import com.example.android53.model.Photo;

import java.util.ArrayList;
import java.util.List;

class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    interface Listener {
        void onPhotoClick(Photo photo);

        void onPhotoLongClick(Photo photo);
    }

    private final Listener listener;
    private final List<Photo> photos = new ArrayList<>();

    PhotoAdapter(Listener listener) {
        this.listener = listener;
    }

    void submit(List<Photo> items) {
        photos.clear();
        photos.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photos.get(position);
        holder.caption.setText(photo.getCaption());
        holder.image.setImageURI((Uri) null);
        holder.image.setImageURI(photo.getUri());
        holder.itemView.setOnClickListener(v -> listener.onPhotoClick(photo));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onPhotoLongClick(photo);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView caption;
        final CardView card;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.photoImage);
            caption = itemView.findViewById(R.id.photoCaption);
            card = (CardView) itemView;
        }
    }
}
