package com.example.socion.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.socion.Dashboard.Movies.MoviesModel;
import com.example.socion.Dashboard.Songs.SongsAdapter;
import com.example.socion.Dashboard.Songs.SongsModel;
import com.example.socion.HelpingClasses.CommonVar;
import com.example.socion.R;
import com.example.socion.databinding.ActivityYoutubeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class YoutubeActivity extends YouTubeBaseActivity {

    ActivityYoutubeBinding binding;

    String videoId;
    String videoTitle;
    Boolean videoType;

    ArrayList<YoutubeModel> model;
    ArrayList<MoviesModel> modelMovie;
    YoutubeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYoutubeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        model = new ArrayList<>();
        modelMovie = new ArrayList<>();
        adapter = new YoutubeAdapter(this, this, modelMovie);
        LinearLayoutManager videoLayout = new LinearLayoutManager(this);
        videoLayout.setOrientation(LinearLayoutManager.VERTICAL);
        binding.videoCollaborative.setLayoutManager(videoLayout);
        binding.videoCollaborative.setAdapter(adapter);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YoutubeActivity.this, RecommendationActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });


        videoType = getIntent().getBooleanExtra(CommonVar.VIDEO_TYPE, false);
        videoId = getIntent().getStringExtra(CommonVar.VIDEO_ID);
        videoTitle = getIntent().getStringExtra(CommonVar.VIDEO_TITLE);

        YouTubePlayer.OnInitializedListener listener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(videoId);
                binding.videoTitle.setText(videoTitle);
                youTubePlayer.play();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.i("yt", youTubeInitializationResult.toString());
                Log.i("yt2", provider.toString());
            }
        };

        binding.youtubePlayerView.initialize(CommonVar.API, listener);
        if (videoType) {
            getData();
        } else {
            songs = new ArrayList<>();
            song_adapter = new SongsAdapter(this, songs, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            binding.videoCollaborative.setLayoutManager(layoutManager);
            binding.videoCollaborative.setAdapter(song_adapter);
            firebaseFetch();
        }

    }

    ArrayList<SongsModel> songs;
    SongsAdapter song_adapter;

    private void firebaseFetch() {
        FirebaseFirestore.getInstance().collection("SONGS").whereEqualTo("Emotion", RecommendationActivity.emotion)
                .whereGreaterThan("videoID", videoId).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.e("Home", documentSnapshot.toString());
                                songs.add(new SongsModel((String) documentSnapshot.get("Song"), (String) documentSnapshot.get("Singer")
                                        , (String) documentSnapshot.get("Genre"), (String) documentSnapshot.get("Album"), (String) documentSnapshot.get("Thumbnail"), (String) documentSnapshot.get("videoID")
                                        , documentSnapshot.getDouble("User-Rating"), documentSnapshot.getDouble("Song_id")));
                            }
                            song_adapter.notifyDataSetChanged();
                        } else {
                            Log.i("Movie", task.getException().getMessage());
                            Toast.makeText(YoutubeActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    String url = CommonVar.URL_COLLABORATIVE;

    private void getData() {
        RequestQueue queue = Volley.newRequestQueue(YoutubeActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject respObj = new JSONObject(response);
                    JSONArray jsonArray = respObj.getJSONArray("recommend");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject responseObj = jsonArray.getJSONObject(i);

                        Double movieID = Double.valueOf(responseObj.getString("item_id"));
                        String movieTitle = responseObj.getString("title");
                        String movieGenre = responseObj.getString("category");
                        String movieDirector = responseObj.getString("director");
                        String movieVideoID = responseObj.getString("videoid");
                        String movieThumb = responseObj.getString("thumbnail");
                        String movieIMDB = responseObj.getString("imdb");
                        String movieDuration = responseObj.getString("duration");
                        String movieStars = responseObj.getString("stars");
                        String movieCensor = responseObj.getString("censor");
                        Double movieReleased = Double.valueOf(responseObj.getString("released"));
                        Double movieRating = Double.valueOf(responseObj.getString("imdb"));
                        modelMovie.add(new MoviesModel(movieTitle, movieDirector,movieStars,movieGenre,movieDuration,movieCensor,movieVideoID,movieThumb,movieReleased,movieRating,movieID));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(YoutubeActivity.this, "Error " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("url", videoTitle);
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }
}