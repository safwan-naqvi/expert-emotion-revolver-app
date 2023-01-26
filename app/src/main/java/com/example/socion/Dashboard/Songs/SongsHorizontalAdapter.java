package com.example.socion.Dashboard.Songs;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socion.Dashboard.Movies.MoviesDetailActivity;
import com.example.socion.Dashboard.YoutubeActivity;
import com.example.socion.HelpingClasses.CommonVar;
import com.example.socion.R;
import com.example.socion.databinding.MovieItemHorizontalBinding;
import com.example.socion.databinding.SongsItemLayoutBinding;

import java.util.ArrayList;

public class SongsHorizontalAdapter extends RecyclerView.Adapter<SongsHorizontalAdapter.ViewHolder> {
    Context context;
    ArrayList<SongsModel> songs;

    public SongsHorizontalAdapter(Context context, ArrayList<SongsModel> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongsHorizontalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongsHorizontalAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.movie_item_horizontal, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongsHorizontalAdapter.ViewHolder holder, int position) {
        SongsModel model = songs.get(position);
        Glide.with(context)
                .load(model.getThumbnail())
                .into(holder.binding.itemImage);
        holder.binding.itemName.setText(model.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, YoutubeActivity.class);
                intent.putExtra(CommonVar.VIDEO_TYPE,"song");
                intent.putExtra(CommonVar.VIDEO_ID, model.getLink());
                intent.putExtra(CommonVar.VIDEO_TITLE, model.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        MovieItemHorizontalBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MovieItemHorizontalBinding.bind(itemView);
        }
    }
}
