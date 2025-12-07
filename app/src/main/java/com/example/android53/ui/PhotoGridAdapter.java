package com.example.android53.ui;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android53.R;
import com.example.android53.model.Photo;

import java.util.List;

public class PhotoGridAdapter extends RecyclerView.Adapter<PhotoGridAdapter.ViewHolder> {

    private final List<Photo> photos;
    private final OnPhotoClickListener listener;

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo);
    }

    public PhotoGridAdapter(List<Photo> photos, OnPhotoClickListener listener) {
        this.photos = photos;
        this.listener = listener;
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
        holder.bind(photo, listener);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView photoImage;
        private final TextView captionView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImage = itemView.findViewById(R.id.photoImage);
            captionView = itemView.findViewById(R.id.photoCaption);
        }

        public void bind(Photo photo, OnPhotoClickListener listener) {
            captionView.setText(photo.getCaption());

            Uri uri = Uri.parse(photo.getFilePath());
            photoImage.setImageURI(uri);

            itemView.setOnClickListener(v -> listener.onPhotoClick(photo));
        }
    }
}
