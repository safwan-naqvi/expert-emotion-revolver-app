package com.example.socion.Dashboard.Songs;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socion.Dashboard.Movies.MoviesAdapter;
import com.example.socion.Dashboard.Movies.MoviesModel;
import com.example.socion.Dashboard.YoutubeActivity;
import com.example.socion.HelpingClasses.CommonVar;
import com.example.socion.R;
import com.example.socion.databinding.SongsItemLayoutBinding;

import java.util.ArrayList;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {

    Context context;
    ArrayList<SongsModel> songs;
    Activity activity;

    public SongsAdapter(Context context, ArrayList<SongsModel> songs, Activity activity) {
        this.context = context;
        this.songs = songs;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SongsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongsAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.songs_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongsAdapter.ViewHolder holder, int position) {
        SongsModel model = songs.get(position);
        if (position < 3) {
            holder.binding.feature.setVisibility(VISIBLE);
        } else {
            holder.binding.feature.setVisibility(View.GONE);
        }
        Glide.with(context)
                .load(model.getThumbnail())
                .into(holder.binding.songImage);
        holder.binding.songName.setText(model.getName());
        holder.binding.songLang.setText(model.getAlbum());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, YoutubeActivity.class);
                intent.putExtra(CommonVar.VIDEO_ID, model.getLink());
                intent.putExtra(CommonVar.VIDEO_TITLE, model.getName());

                activity.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SongsItemLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SongsItemLayoutBinding.bind(itemView);
        }
    }
}
