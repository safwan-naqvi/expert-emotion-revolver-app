package com.example.socion.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.socion.Dashboard.DashboardActivity;
import com.example.socion.R;
import com.example.socion.databinding.ActivitySetNewPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetNewPasswordActivity extends AppCompatActivity {

    ActivitySetNewPasswordBinding binding;
    String resetPasswordNumber,uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetNewPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        resetPasswordNumber = getIntent().getStringExtra("phoneNo");
        uid = getIntent().getStringExtra("uid");
        Toast.makeText(this, uid+" ", Toast.LENGTH_SHORT).show();
        binding.resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

    }

    private void resetPassword() {
        if(!validateConfirmPassword()){
            return;
        }else{
            FirebaseFirestore.getInstance().collection("USERS").whereEqualTo("phone", resetPasswordNumber)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                    FirebaseFirestore.getInstance().collection("USERS").document(documentSnapshot.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                            String pass = binding.confirmPasswordReset.getEditText().getText().toString().trim();
                                            Map<String,Object> password = new HashMap<>();
                                            password.put("password",pass);
                                            FirebaseFirestore.getInstance().collection("USERS").document(documentSnapshot.getId()).set(password, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    startActivity(new Intent(SetNewPasswordActivity.this, LoginActivity.class));
                                                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                                    finish();
                                                }
                                            });

                                        }
                                    });

                                }
                            }
                        }
                    });


        }
    }
    public boolean validatePassword() {
        String val = binding.passwordReset.getEditText().getText().toString().trim();
        // Creating a Pattern class object
        Pattern p = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$");
        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression for which
        // object of Matcher class is created
        Matcher m = p.matcher(val);
        if (val.isEmpty()) {
            binding.passwordReset.setError("Field Cannot Be Empty");
            return false;
        } else if (!m.matches()) {
            binding.passwordReset.setError("Password length should be between 8 and 20\n" +
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
            binding.passwordReset.setError(null);
            binding.passwordReset.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        if(!validatePassword()){
            return false;
        }else{
            String val = binding.confirmPasswordReset.getEditText().getText().toString().trim();
            if(val.isEmpty()){
                binding.confirmPasswordReset.setError("Field Cannot Be Empty");
                return false;
            }else{
                if(!val.equals(binding.passwordReset.getEditText().getText().toString())){
                    binding.confirmPasswordReset.setError("Confirm Password Must Be Same");
                    return false;
                }else{
                    binding.confirmPasswordReset.setError(null);
                    binding.confirmPasswordReset.setErrorEnabled(false);
                    return true;
                }
            }
        }
    }

}