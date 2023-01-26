package com.example.socion.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.socion.Dashboard.Movies.MoviesAdapter;
import com.example.socion.Dashboard.Movies.MoviesModel;
import com.example.socion.databinding.ActivityScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ActivityScreenBinding binding;
    MoviesAdapter moviesAdapter;
    ArrayList<MoviesModel> moviesModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        moviesModels = new ArrayList<>();
        moviesAdapter = new MoviesAdapter(this, moviesModels,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.searchList.setLayoutManager(layoutManager);
        binding.searchList.setAdapter(moviesAdapter);
        String query = getIntent().getStringExtra("query");
        getProducts(query);

    }

    void getProducts(String query) {
        String str = query;
        String cap = str.substring(0, 1).toUpperCase() + str.substring(1);
        FirebaseFirestore.getInstance().collection("MOVIES")
                .whereGreaterThanOrEqualTo("Title",cap)
                .limit(10).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.e("Home", documentSnapshot.toString());
                                moviesModels.add(new MoviesModel((String) documentSnapshot.get("Title"), (String) documentSnapshot.get("Director")
                                        , (String) documentSnapshot.get("Stars"), (String) documentSnapshot.get("Category"), (String) documentSnapshot.get("Duration")
                                        , (String) documentSnapshot.get("Censor"), (String) documentSnapshot.get("videoID"), (String) documentSnapshot.get("thumbnail")
                                        , documentSnapshot.getDouble("Released"), documentSnapshot.getDouble("IMDB"), documentSnapshot.getDouble("itemID")));

                            }
                            moviesAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SearchActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}