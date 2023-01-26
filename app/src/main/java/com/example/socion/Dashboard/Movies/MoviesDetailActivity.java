package com.example.socion.Dashboard.Movies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socion.Dashboard.RecommendationActivity;
import com.example.socion.Dashboard.YoutubeActivity;
import com.example.socion.HelpingClasses.CommonVar;
import com.example.socion.databinding.ActivityMoviesDetailBinding;

public class MoviesDetailActivity extends AppCompatActivity {

    ActivityMoviesDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMoviesDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MoviesModel myObject = (MoviesModel) getIntent().getSerializableExtra("movie");

        Glide.with(this)
                .load(myObject.getImage())
                .into(binding.movieImage);

        binding.movieName.setText(myObject.getTitle());
        binding.movieTime.setText(myObject.getDuration());
        binding.movieActors.setText(myObject.getStarring());
        binding.movieDirector.setText(myObject.getDirector());
        binding.movieGenre.setText(myObject.getCategory());
        binding.movieRating.setText(myObject.getRating()+"");
        binding.watchTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MoviesDetailActivity.this, YoutubeActivity.class)
                        .putExtra(CommonVar.VIDEO_TITLE,myObject.getTitle())
                        .putExtra(CommonVar.VIDEO_ID,myObject.getVideoID())
                        .putExtra(CommonVar.VIDEO_TYPE,true));

            }
        });

        binding.backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoviesDetailActivity.this, RecommendationActivity.class);
                intent.putExtra("emotion",RecommendationActivity.emotion);
                startActivity(intent);
                finish();
            }
        });

    }
}