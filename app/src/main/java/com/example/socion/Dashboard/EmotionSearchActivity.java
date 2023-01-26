package com.example.socion.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.socion.Dashboard.Movies.MoviesAdapter;
import com.example.socion.Dashboard.Movies.MoviesModel;
import com.example.socion.R;
import com.example.socion.databinding.ActivityEmotionSearchBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EmotionSearchActivity extends AppCompatActivity {

    ActivityEmotionSearchBinding binding;
    MoviesAdapter moviesAdapter;
    ArrayList<MoviesModel> moviesModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmotionSearchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        String query = getIntent().getStringExtra("query");
        moviesModels = new ArrayList<>();
        moviesAdapter = new MoviesAdapter(this, moviesModels,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.searchEmotionList.setLayoutManager(layoutManager);
        binding.searchEmotionList.setAdapter(moviesAdapter);
        binding.activityTitleDesc.setText("Search By "+query+" Emotion");

        getProducts(query);

    }
    void getProducts(String query) {
        FirebaseFirestore.getInstance().collection("MOVIES")
                .whereGreaterThanOrEqualTo("Final Emotion",query)
                .limit(20).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            Toast.makeText(EmotionSearchActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmotionSearchActivity.this,RecommendationActivity.class);
                intent.putExtra("emotion",RecommendationActivity.emotion);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(EmotionSearchActivity.this,RecommendationActivity.class);
        intent.putExtra("emotion",RecommendationActivity.emotion);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

}