package com.example.urlshortenerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urlshortener.R;

import java.util.List;

public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.UrlViewHolder> {
    private List<ShortenedUrl> urls;
    private UrlRepository urlRepository;

    public UrlAdapter(List<ShortenedUrl> urls, UrlRepository urlRepository) {
        this.urls = urls;
        this.urlRepository = urlRepository;
    }

    @NonNull
    @Override
    public UrlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_url, parent, false);
        return new UrlViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UrlViewHolder holder, int position) {
        ShortenedUrl url = urls.get(position);
        holder.tvOriginalUrl.setText(url.originalUrl);
        holder.tvShortUrl.setText(url.shortUrl);

        holder.btnDelete.setOnClickListener(v -> {
            urlRepository.deleteUrl(url.id);
            urls.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(holder.itemView.getContext(), "URL eliminada", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    public static class UrlViewHolder extends RecyclerView.ViewHolder {
        TextView tvOriginalUrl, tvShortUrl;
        Button btnDelete;

        public UrlViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOriginalUrl = itemView.findViewById(R.id.tvOriginalUrl);
            tvShortUrl = itemView.findViewById(R.id.tvShortUrl);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
