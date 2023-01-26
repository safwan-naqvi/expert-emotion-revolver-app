package com.example.socion.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.socion.Dashboard.DashboardActivity;
import com.example.socion.R;
import com.example.socion.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.notUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        binding.forgetLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAuthentication();
            }
        });

    }

    private void loginAuthentication() {
        if (!validatePhoneNumber() || !validatePassword()) {
            return;
        } else {
            String countryCode = binding.countryCodePickLogin.getSelectedCountryCode();
            //Gathered Verified Inputs From Registeration Form
            String number = "+" + countryCode + binding.phoneLogin.getEditText().getText().toString();
            String password = binding.passwordLogin.getEditText().getText().toString();
            Log.i("number", number);
            FirebaseFirestore.getInstance().collection("USERS").whereEqualTo("phone", number)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                    FirebaseFirestore.getInstance().collection("USERS").document(documentSnapshot.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (documentSnapshot.getString("password").equals(password)) {
                                                startActivity(new Intent(LoginActivity.this, OTPActivity.class)
                                                        .putExtra("number",number)
                                                        .putExtra("password",password)
                                                        .putExtra("type","login"));
                                                finish();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            }
                        }
                    });
        }
    }

    private boolean validatePhoneNumber() {
        String val = binding.phoneLogin.getEditText().getText().toString().trim();
        // Creating a Pattern class object
        Pattern p = Pattern.compile("^\\d{10}$");

        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression for which
        // object of Matcher class is created
        Matcher m = p.matcher(val);
        if (val.isEmpty()) {
            binding.phoneLogin.setError("Field Cannot Be Empty");
            return false;
        } else if (!m.matches()) {
            binding.phoneLogin.setError("No White spaces are allowed!");
            return false;
        } else {
            binding.phoneLogin.setError(null);
            binding.phoneLogin.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validatePassword() {
        String val = binding.passwordLogin.getEditText().getText().toString().trim();
        // Creating a Pattern class object
        Pattern p = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$");
        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression for which
        // object of Matcher class is created
        Matcher m = p.matcher(val);
        if (val.isEmpty()) {
            binding.passwordLogin.setError("Field Cannot Be Empty");
            return false;
        } else if (!m.matches()) {
            binding.passwordLogin.setError("Password length should be between 8 and 20\n" +
                    "At least one numeric character!\n" +
                    "Must have at least one lowercase character\n" +
                    "Must have at least one uppercase character\n" +
                    "Must have at least one special symbol among @#$%");
            //Display all Must have at least one numeric character
            //Must have at least one lowercase character
            //Must have at least one uppercase character
            //Must have at least one special symbol among @#$%
            //Password length should be between 8 and 20
            return false;
        } else {
            binding.passwordLogin.setError(null);
            binding.passwordLogin.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}