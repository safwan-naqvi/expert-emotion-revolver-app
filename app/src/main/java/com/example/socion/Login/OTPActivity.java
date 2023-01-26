package com.example.socion.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.socion.Dashboard.DashboardActivity;
import com.example.socion.R;
import com.example.socion.databinding.ActivityOtpactivityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    ActivityOtpactivityBinding binding;
    String userPhoneNumber, userName, userPassword;
    String otpType;
    FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    PhoneAuthProvider.ForceResendingToken token;
    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getValuesFromPreviewIntent();
        initCallbacks();
        Log.e("OTP",userPhoneNumber);

        verifyPhoneNumber(userPhoneNumber);

        binding.verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.otpPinView.getText().toString().isEmpty()) {
                    binding.otpPinView.setError("Must Provide OTP");
                    return;
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, binding.otpPinView.getText().toString());
                authenticateUser(credential);
            }
        });

    }

    private void getValuesFromPreviewIntent() {
        if (getIntent().getExtras() != null) {
            otpType = getIntent().getStringExtra("type");
            if (otpType.equals("register")) {
                userName = getIntent().getStringExtra("name");
                userPhoneNumber = getIntent().getStringExtra("number");
                userPassword = getIntent().getStringExtra("password");
            }else if(otpType.equals("login")){
                userPhoneNumber = getIntent().getStringExtra("number");
                userPassword = getIntent().getStringExtra("password");
            }
            else{
                userPhoneNumber = getIntent().getStringExtra("number");
                binding.textView4.setText("Verify OTP To Reset Your Password");
            }
        }

    }

    private void initCallbacks() {
        mAuth = FirebaseAuth.getInstance();
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                authenticateUser(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                showToast(e.toString());
                binding.resendOTP.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                token = forceResendingToken;

                binding.resendOTP.setVisibility(View.GONE);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                binding.resendOTP.setVisibility(View.VISIBLE);
            }
        };

    }

    public void showToast(String s) {
        Toast.makeText(OTPActivity.this, s, Toast.LENGTH_SHORT).show();
        Log.e("OTP",s);
    }

    public void verifyPhoneNumber(String phoneNo) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setActivity(this)
                .setPhoneNumber(phoneNo)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void authenticateUser(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if(otpType.equals("register")){

                    Intent intent = new Intent(OTPActivity.this,SetupProfileActivity.class);
                    intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    intent.putExtra("name",userName);
                    intent.putExtra("phone",userPhoneNumber);
                    intent.putExtra("password",userPassword);

                    startActivity(intent);
                    //Not using finish because user can come back to change if wrong

                }else if(otpType.equals("login")){
                    Intent intent = new Intent(OTPActivity.this,DashboardActivity.class);
                    startActivity(intent);
                }
                else{
                    resetPassword();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(e.toString());
                startActivity(new Intent(OTPActivity.this,RegisterActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        });
    }

    private void resetPassword() {
        Intent intent = new Intent(OTPActivity.this, SetNewPasswordActivity.class);
        intent.putExtra("phoneNo", userPhoneNumber);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }
}