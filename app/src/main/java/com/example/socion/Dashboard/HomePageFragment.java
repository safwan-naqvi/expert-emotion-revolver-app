package com.example.socion.Dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socion.Dashboard.Movies.HorizontalMovieAdapter;
import com.example.socion.Dashboard.Movies.MoviesAdapter;
import com.example.socion.Dashboard.Movies.MoviesModel;
import com.example.socion.Dashboard.Songs.SongsAdapter;
import com.example.socion.Dashboard.Songs.SongsHorizontalAdapter;
import com.example.socion.Dashboard.Songs.SongsModel;
import com.example.socion.R;
import com.example.socion.databinding.FragmentHomePageBinding;
import com.example.socion.databinding.FragmentMoviesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.Random;

public class HomePageFragment extends Fragment {

    FragmentHomePageBinding binding;

    ArrayList<MoviesModel> movies;
    ArrayList<SongsModel> songs;
    HorizontalMovieAdapter adapter;
    SongsHorizontalAdapter songsAdapter;
    FirebaseFirestore firebaseFirestore;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomePageBinding.inflate(inflater, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        initMovies();
        initSongs();

        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("query",text.toString());
                startActivity(intent);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


        binding.neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EmotionSearchActivity.class);
                intent.putExtra("query","Neutral");
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                startActivity(intent);
                getActivity().finish();
            }
        });

        binding.angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EmotionSearchActivity.class);
                intent.putExtra("query","Angry");
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                startActivity(intent);
                getActivity().finish();
            }
        });

        binding.happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EmotionSearchActivity.class);
                intent.putExtra("query","Happy");
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                startActivity(intent);
                getActivity().finish();
            }
        });

        binding.sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EmotionSearchActivity.class);
                intent.putExtra("query","Sad");
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                startActivity(intent);
                getActivity().finish();
            }
        });
        double alpha = Math.random()*(2015-2022+1)+2015;
        //region Categories List.orderBy("Released", Query.Direction.DESCENDING)
        firebaseFirestore.collection("MOVIES").whereGreaterThanOrEqualTo("Released",alpha).limit(8).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.e("Home", documentSnapshot.toString());
                                movies.add(new MoviesModel((String)documentSnapshot.get("Title"), (String)documentSnapshot.get("Director")
                                        , (String)documentSnapshot.get("Stars"), (String)documentSnapshot.get("Category"),(String)documentSnapshot.get("Duration")
                                        , (String)documentSnapshot.get("Censor"), (String)documentSnapshot.get("videoID"),(String)documentSnapshot.get("thumbnail")
                                        ,documentSnapshot.getDouble("Released"),documentSnapshot.getDouble("IMDB"), documentSnapshot.getDouble("itemID")));

                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //endregion

//region Songs List
        double a = Math.random()*(250-10+1)+10;
        firebaseFirestore.collection("SONGS").whereGreaterThanOrEqualTo("Song_id",a).limit(8).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.e("Home", documentSnapshot.toString());
                                songs.add(new SongsModel((String)documentSnapshot.get("Song"), (String)documentSnapshot.get("Singer")
                                        , (String)documentSnapshot.get("Genre"), (String)documentSnapshot.get("Album"),(String)documentSnapshot.get("Thumbnail"),(String)documentSnapshot.get("videoID")
                                        ,documentSnapshot.getDouble("User-Rating"),documentSnapshot.getDouble("Song_id")));
                            }
                            songsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //endregion



        return binding.getRoot();
    }


    private void initMovies() {
        movies = new ArrayList<>();
        adapter = new HorizontalMovieAdapter(getContext(), movies);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.popularMovies.setLayoutManager(layoutManager);
        binding.popularMovies.setAdapter(adapter);
    }

    private void initSongs() {
        songs = new ArrayList<>();
        songsAdapter = new SongsHorizontalAdapter(getContext(), songs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.popularSongs.setLayoutManager(layoutManager);
        binding.popularSongs.setAdapter(songsAdapter);
    }

}