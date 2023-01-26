package com.example.socion.Dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socion.Dashboard.Movies.MoviesAdapter;
import com.example.socion.Dashboard.Movies.MoviesModel;
import com.example.socion.Dashboard.Songs.SongsAdapter;
import com.example.socion.Dashboard.Songs.SongsModel;
import com.example.socion.R;
import com.example.socion.databinding.FragmentMoviesBinding;
import com.example.socion.databinding.FragmentSongsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SongsFragment extends Fragment {

    FragmentSongsBinding binding;
    ArrayList<SongsModel> songs;
    SongsAdapter adapter;


    public SongsFragment() {
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
        binding = FragmentSongsBinding.inflate(inflater, container, false);
        initProduct();

        FirebaseFirestore.getInstance().collection("SONGS").whereEqualTo("Emotion", RecommendationActivity.emotion)
                .whereGreaterThanOrEqualTo("Song_id",0).limit(10).get()
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
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.i("Movie", task.getException().getMessage());
                            Toast.makeText(getContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return binding.getRoot();
    }
    private void initProduct() {
        songs = new ArrayList<>();
        adapter = new SongsAdapter(getContext(), songs,getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.songsRecycler.setLayoutManager(layoutManager);
        binding.songsRecycler.setAdapter(adapter);
    }

}