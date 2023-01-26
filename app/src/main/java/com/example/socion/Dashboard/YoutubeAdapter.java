package com.example.socion.Dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.socion.Dashboard.Movies.MoviesDetailActivity;
import com.example.socion.Dashboard.Movies.MoviesModel;
import com.example.socion.HelpingClasses.Common;
import com.example.socion.HelpingClasses.CommonVar;
import com.example.socion.R;
import com.example.socion.databinding.YoutubeVideoItemBinding;

import java.util.ArrayList;

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.ViewHolder> {

    Context context;
    Activity activity;
    //ArrayList<YoutubeModel> model;
    ArrayList<MoviesModel> model;

    public YoutubeAdapter(Context context, Activity activity, ArrayList<MoviesModel> model) {
        this.context = context;
        this.activity = activity;
        this.model = model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.youtube_video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //YoutubeModel youtube = model.get(position);
        MoviesModel youtube = model.get(position);
        String videoId = youtube.getVideoID();
        String videoTitle = youtube.getTitle();
        String videoDirector = youtube.getDirector();
        String videoStar = youtube.getStarring();
        String videoGenre = youtube.getCategory();
        String videoRating = String.valueOf(youtube.getRating());

        holder.binding.videoTitle.setText(videoTitle);
        holder.binding.videoGenre.setText(videoGenre);
        holder.binding.videoRating.setText(videoRating);
        holder.binding.videoStarring.setText(videoStar);
        setImage(youtube.getImage(),holder.binding.videoThumbnail);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(activity, YoutubeActivity.class);
//                intent.putExtra(CommonVar.VIDEO_ID,videoId);
//                intent.putExtra(CommonVar.VIDEO_TITLE,videoTitle);
//                activity.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
//                activity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
//            }
//        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MoviesDetailActivity.class);
                intent.putExtra("movie", (MoviesModel) youtube);
                intent.putExtra(CommonVar.VIDEO_TYPE, "Movie");
                activity.startActivity(intent);
                activity.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                activity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        YoutubeVideoItemBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = YoutubeVideoItemBinding.bind(itemView);
        }
    }

    public void setImage(String url, ImageView videoThumbnail){
        Glide.with(activity).load(url)
                .apply(new RequestOptions()
                        .fitCenter()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL))
                .placeholder(R.drawable.ic_placeholder)
                .into(videoThumbnail);
    }

}

