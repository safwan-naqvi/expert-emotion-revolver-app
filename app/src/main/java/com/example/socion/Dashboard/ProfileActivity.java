package com.example.socion.Dashboard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socion.HelpingClasses.Common;
import com.example.socion.HelpingClasses.CommonVar;
import com.example.socion.HelpingClasses.PreferenceHelper;
import com.example.socion.HelpingClasses.User;
import com.example.socion.Login.LoginActivity;
import com.example.socion.Login.SetupProfileActivity;
import com.example.socion.R;
import com.example.socion.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("USERS").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    changeData(value);
                }
            }
        });


        binding.signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        binding.profileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class)
                        .putExtra("uid",Common.uid)
                        .putExtra("phone",Common.u_phone)
                        .putExtra("password",Common.u_password)
                        .putExtra("name",Common.u_name)
                        .putExtra("profile",Common.u_profile));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }
    User user;
    private void changeData(DocumentSnapshot value) {
        user = value.toObject(User.class);
        if (user != null) {
            Common.u_name = user.getName();
            Common.u_phone = user.getPhone();
            Common.u_password = user.getPassword();
            Common.u_profile = user.getProfile();
            Common.uid = user.getUid();
        }

        //region Setting up fetched Data
        binding.userProfileName.setText("Hi, " + Common.u_name);
        Glide.with(getApplicationContext())
                .load(Common.u_profile)
                .centerCrop()
                .placeholder(R.drawable.avatar)
                .into(binding.userProfileDp);
        binding.userProfileNumber.setText(Common.u_phone);
        //endregion



    }
}