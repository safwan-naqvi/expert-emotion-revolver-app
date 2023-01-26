package com.example.socion.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.socion.R;
import com.example.socion.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        binding.alreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    private boolean validatePhoneNumber() {
        String val = binding.numberRegister.getEditText().getText().toString().trim();
        // Creating a Pattern class object
        Pattern p = Pattern.compile("^\\d{10}$");

        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression for which
        // object of Matcher class is created
        Matcher m = p.matcher(val);
        if (val.isEmpty()) {
            binding.numberRegister.setError("Field Cannot Be Empty");
            return false;
        } else if (!m.matches()) {
            binding.numberRegister.setError("No White spaces are allowed!");
            return false;
        } else {
            binding.numberRegister.setError(null);
            binding.numberRegister.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validatePassword() {
        String val = binding.passwordRegister.getEditText().getText().toString().trim();
        // Creating a Pattern class object
        Pattern p = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$");
        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression for which
        // object of Matcher class is created
        Matcher m = p.matcher(val);
        if (val.isEmpty()) {
            binding.passwordRegister.setError("Field Cannot Be Empty");
            return false;
        } else if (!m.matches()) {
            binding.passwordRegister.setError("Password length should be between 8 and 20\n" +
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
            binding.passwordRegister.setError(null);
            binding.passwordRegister.setErrorEnabled(false);
            return true;
        }

    }

    public boolean validateName() {
        String val = binding.nameRegister.getEditText().getText().toString().trim();
        // Creating a Pattern class object
        Pattern p = Pattern.compile("^[A-Z](?=.{1,29}$)[A-Za-z]*(?:\\h+[A-Z][A-Za-z]*)*$");
        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression for which
        // object of Matcher class is created
        Matcher m = p.matcher(val);
        if (val.isEmpty()) {
            binding.nameRegister.setError("Field Cannot Be Empty");
            return false;

        } else {
            binding.nameRegister.setError(null);
            binding.nameRegister.setErrorEnabled(false);
            return true;
        }
    }

    public void registerUser() {
        if (!validatePhoneNumber() || !validateName() || !validatePassword()) {
            return;
        } else {
            String countryCode = binding.countryCodePickLogin.getSelectedCountryCode();
            //Gathered Verified Inputs From Registeration Form
            String number = "+" + countryCode + binding.numberRegister.getEditText().getText().toString();
            String userName = binding.nameRegister.getEditText().getText().toString();
            String password = binding.passwordRegister.getEditText().getText().toString();
            //Passing Values to OTP Screen but we can
            //also add functionality over here
            //to first search that either given
            //number already exists in database or not
            //it will be done using firebase firestore query but we will
            //add that thing later and below
            //part must be in Else part of query
            FirebaseFirestore.getInstance().collection("USERS").document(number)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Toast.makeText(RegisterActivity.this, "Already Registered With this number", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(RegisterActivity.this, OTPActivity.class);
                                intent.putExtra("name", userName);
                                intent.putExtra("number", number);
                                intent.putExtra("password", password);
                                intent.putExtra("type", "register");
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        }
                    });

        }
    }

}

