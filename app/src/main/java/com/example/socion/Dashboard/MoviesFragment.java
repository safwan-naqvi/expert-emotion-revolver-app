package com.example.socion.Dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socion.Dashboard.Movies.MoviesAdapter;
import com.example.socion.Dashboard.Movies.MoviesModel;
import com.example.socion.R;
import com.example.socion.databinding.FragmentMoviesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MoviesFragment extends Fragment {

    FragmentMoviesBinding binding;
    ArrayList<MoviesModel> movies;
    MoviesAdapter adapter;

    public MoviesFragment() {
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
        binding = FragmentMoviesBinding.inflate(inflater, container, false);
        initProduct();
        Log.i("Movie", RecommendationActivity.emotion);
        FirebaseFirestore.getInstance().collection("MOVIES").whereEqualTo("Final Emotion", RecommendationActivity.emotion)
                .whereGreaterThanOrEqualTo("item_id", 0).limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.e("Home", documentSnapshot.toString());
                                movies.add(new MoviesModel((String) documentSnapshot.get("Title"), (String) documentSnapshot.get("Director")
                                        , (String) documentSnapshot.get("Stars"), (String) documentSnapshot.get("Category"), (String) documentSnapshot.get("Duration")
                                        , (String) documentSnapshot.get("Censor"), (String) documentSnapshot.get("videoID"), (String) documentSnapshot.get("thumbnail")
                                        , documentSnapshot.getDouble("Released"), documentSnapshot.getDouble("IMDB"), documentSnapshot.getDouble("itemID")));

                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.i("Movie", task.getException().getMessage());
                            //Toast.makeText(getContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        return binding.getRoot();
    }


    private void initProduct() {
        movies = new ArrayList<>();
        adapter = new MoviesAdapter(getContext(), movies,getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.moviesRecycler.setLayoutManager(layoutManager);
        binding.moviesRecycler.setAdapter(adapter);
    }

}