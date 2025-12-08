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
import com.example.android53.data.SearchResult;

import java.util.ArrayList;
import java.util.List;

class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    interface Listener {
        void onResultClick(SearchResult result);
    }

    private final Listener listener;
    private final List<SearchResult> results = new ArrayList<>();

    SearchResultAdapter(Listener listener) {
        this.listener = listener;
    }

    void submit(List<SearchResult> items) {
        results.clear();
        results.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResult result = results.get(position);
        holder.caption.setText(result.getPhoto().getCaption());
        holder.album.setText("Album: " + result.getAlbum().getName());
        holder.image.setImageURI((Uri) null);
        holder.image.setImageURI(result.getPhoto().getUri());
        holder.itemView.setOnClickListener(v -> listener.onResultClick(result));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView caption;
        final TextView album;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.resultImage);
            caption = itemView.findViewById(R.id.resultCaption);
            album = itemView.findViewById(R.id.resultAlbum);
        }
    }
}
