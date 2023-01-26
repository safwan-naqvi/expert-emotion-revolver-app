package com.example.socion.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.socion.R;
import com.example.socion.databinding.ActivityForgotBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotActivity extends AppCompatActivity {
    ActivityForgotBinding binding;
    String forgotPasswordNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOtp();
            }
        });

    }

    public void sendOtp() {
        if (!validatePhoneNumber()) {
            return;
        } else {
            String countryCode = binding.countryCodePickLogin.getSelectedCountryCode();
            forgotPasswordNumber = binding.forgetterPasswordNumber.getText().toString();
            String number = "+" + countryCode + forgotPasswordNumber;
            Toast.makeText(this, number, Toast.LENGTH_SHORT).show();
            FirebaseFirestore.getInstance().collection("USERS").whereEqualTo("phone", number)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                    FirebaseFirestore.getInstance().collection("USERS").document(documentSnapshot.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                            startActivity(new Intent(ForgotActivity.this, OTPActivity.class)
                                                    .putExtra("number", number)
                                                    .putExtra("uid",documentSnapshot.getId()+"")
                                                    .putExtra("type", "logxsain"));
                                            finish();

                                        }
                                    });

                                }
                            }
                        }
                    });


        }
    }

    private boolean validatePhoneNumber() {
        return true;
        // Creating a Pattern class object
//        Pattern p = Pattern.compile("^\\d{10}$");
//        // Pattern class contains matcher() method
//        // to find matching between given number
//        // and regular expression for which
//        // object of Matcher class is created
//        Matcher m = p.matcher(forgotPasswordNumber);
//        if (forgotPasswordNumber.isEmpty()) {
//            binding.forgetterPasswordNumber.setError("Field Cannot Be Empty");
//            return false;
//        } else if (!m.matches()) {
//            binding.forgetterPasswordNumber.setError("No White spaces are allowed!");
//            return false;
//        } else {
//            binding.forgetterPasswordNumber.setError(null);
//            //binding.forgetterPasswordNumber.setErrorEnabled(false);
//            return true;
//        }

    }

}