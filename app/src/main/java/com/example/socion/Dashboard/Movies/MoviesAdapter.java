package com.example.socion.Dashboard.Movies;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socion.Dashboard.YoutubeActivity;
import com.example.socion.HelpingClasses.CommonVar;
import com.example.socion.R;
import com.example.socion.databinding.MoviesItemLayoutBinding;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    Context context;
    ArrayList<MoviesModel> movies;
    Activity activity;

    public MoviesAdapter(Context context, ArrayList<MoviesModel> movies, Activity activity) {
        this.context = context;
        this.movies = movies;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.movies_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.ViewHolder holder, int position) {
        MoviesModel model = movies.get(position);
        if (position < 3) {
            holder.binding.feature.setVisibility(VISIBLE);
        } else {
            holder.binding.feature.setVisibility(View.GONE);
        }
        Glide.with(context)
                .load(model.getImage())
                .into(holder.binding.movieImage);
        holder.binding.movieName.setText(model.getTitle());
        holder.binding.movieGenre.setText(model.getCategory());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MoviesDetailActivity.class);
                intent.putExtra("movie", model);
                intent.putExtra(CommonVar.VIDEO_TYPE, "Movie");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MoviesItemLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MoviesItemLayoutBinding.bind(itemView);
        }
    }
}
