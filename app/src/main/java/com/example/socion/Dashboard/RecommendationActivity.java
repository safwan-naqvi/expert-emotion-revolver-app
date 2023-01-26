package com.example.socion.Dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;

import com.example.socion.R;
import com.example.socion.databinding.ActivityRecommendationBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class RecommendationActivity extends AppCompatActivity {

    ActivityRecommendationBinding binding;

    public static String emotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecommendationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.chipNavigationBar.setItemSelected(R.id.movies,
                true);

        if (getIntent().getExtras() != null) {
            emotion = getIntent().getStringExtra("emotion");
            if(emotion.equals("Angry") || emotion.equals("Sad")){
                emotion = "Happy";
            }
        }else{
            emotion = "Neutral";
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recommedation_holder, new MoviesFragment()).commit();
        bottomMenu();
    }

    private void bottomMenu() {
        binding.chipNavigationBar.setOnItemSelectedListener
                (new ChipNavigationBar.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int i) {
                        Fragment fragment = null;
                        switch (i) {
                            case R.id.home:
                                fragment = new HomePageFragment();
                                break;
                            case R.id.movies:
                                fragment = new MoviesFragment();
                                break;
                            case R.id.songs:
                                fragment = new SongsFragment();
                                break;
                        }
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.recommedation_holder,
                                        fragment).commit();
                    }
                });
    }
}