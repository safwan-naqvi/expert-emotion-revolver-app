package com.example.socion.Dashboard.Movies;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socion.HelpingClasses.CommonVar;
import com.example.socion.R;
import com.example.socion.databinding.MovieItemHorizontalBinding;
import com.example.socion.databinding.MoviesItemLayoutBinding;

import java.util.ArrayList;

public class HorizontalMovieAdapter extends RecyclerView.Adapter<HorizontalMovieAdapter.ViewHolder>{
    Context context;
    ArrayList<MoviesModel> movies;

    public HorizontalMovieAdapter(Context context, ArrayList<MoviesModel> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public HorizontalMovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HorizontalMovieAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.movie_item_horizontal, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalMovieAdapter.ViewHolder holder, int position) {
        MoviesModel model = movies.get(position);
        Glide.with(context)
                .load(model.getImage())
                .into(holder.binding.itemImage);
        holder.binding.itemName.setText(model.getTitle());
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
        MovieItemHorizontalBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MovieItemHorizontalBinding.bind(itemView);
        }
    }
}
