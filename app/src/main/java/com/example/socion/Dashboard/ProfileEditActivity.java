package com.example.socion.Dashboard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socion.HelpingClasses.Common;
import com.example.socion.Login.SetupProfileActivity;
import com.example.socion.R;
import com.example.socion.databinding.ActivityProfileEditBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileEditActivity extends AppCompatActivity {

    ActivityProfileEditBinding binding;
    String DownloadImageUrl;
    private Uri imageUri;
    StorageReference UserImagesRef;
    ActivityResultLauncher<Intent> startForProfileImageResult;
    String uid, image, name, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        UserImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        uid = getIntent().getStringExtra("uid");
        image = getIntent().getStringExtra("profile");
        name = getIntent().getStringExtra("name");
        pass = getIntent().getStringExtra("password");

        binding.userProfileName.getEditText().setText(name);
        binding.userProfilePassword.getEditText().setText(pass);

        setUserImage();

        Glide.with(getApplicationContext())
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.avatar)
                .into(binding.userImage);
        binding.updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isNetworkAvailable(ProfileEditActivity.this)) {

                    SaveProductInformationToDB();

                } else {
                    Toast.makeText(ProfileEditActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setUserImage() {
        startForProfileImageResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();

                        if (resultCode == Activity.RESULT_OK && data != null) {
                            imageUri = data.getData();
                            binding.userImage.setImageURI(imageUri);
                        } else if (resultCode == ImagePicker.RESULT_ERROR) {
                            Toast.makeText(ProfileEditActivity.this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileEditActivity.this, "Task Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProfileEditActivity.this)
                        .compress(1024)         //Final image size will be less than 1 MB(Optional)
                        .crop()
                        .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                startForProfileImageResult.launch(intent);
                                return null;
                            }
                        });
            }
        });
    }

    private void StoreProductImageInformation() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());

        String productRandomKey = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = UserImagesRef.child(imageUri.getLastPathSegment() + productRandomKey);

        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.getMessage().toString();
                Toast.makeText(ProfileEditActivity.this, "Error " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        DownloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            DownloadImageUrl = task.getResult().toString();
                           // SaveProductInformationToDB(DownloadImageUrl);
                            HashMap<String, Object> userInfo = new HashMap<>();
                            userInfo.put("name", binding.userProfileName.getEditText().getText().toString());
                            userInfo.put("password", binding.userProfilePassword.getEditText().getText().toString());
                            userInfo.put("profile", DownloadImageUrl);
                            FirebaseFirestore.getInstance().collection("USERS").document(uid)
                                    .set(userInfo, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            startActivity(new Intent(ProfileEditActivity.this, ProfileActivity.class));
                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            finishAffinity();
                                        }
                                    });
                        } else {
                            String msg = task.getException().toString();
                            Toast.makeText(getApplicationContext(), "Error " + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }


    private void SaveProductInformationToDB() {
        if (!validateName() || !validatePassword()) {
            return;
        } else {
            HashMap<String, Object> userInfo = new HashMap<>();
            if (imageUri != null) {
                StoreProductImageInformation();
            }else{
                userInfo.put("name", binding.userProfileName.getEditText().getText().toString());
                userInfo.put("password", binding.userProfilePassword.getEditText().getText().toString());
                userInfo.put("profile", image);
            }
            FirebaseFirestore.getInstance().collection("USERS").document(uid)
                    .set(userInfo, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(ProfileEditActivity.this, ProfileActivity.class));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finishAffinity();
                        }
                    });
        }
    }

    public boolean validatePassword() {
        String val = binding.userProfilePassword.getEditText().getText().toString().trim();
        // Creating a Pattern class object
        Pattern p = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$");
        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression for which
        // object of Matcher class is created
        Matcher m = p.matcher(val);
        if (val.isEmpty()) {
            binding.userProfilePassword.setError("Field Cannot Be Empty");
            return false;
        } else if (!m.matches()) {
            binding.userProfilePassword.setError("Password length should be between 8 and 20\n" +
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
            binding.userProfilePassword.setError(null);
            binding.userProfilePassword.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validateName() {
        String val = binding.userProfileName.getEditText().getText().toString().trim();
        // Creating a Pattern class object
        Pattern p = Pattern.compile("^[A-Z](?=.{1,29}$)[A-Za-z]*(?:\\h+[A-Z][A-Za-z]*)*$");
        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression for which
        // object of Matcher class is created
        Matcher m = p.matcher(val);
        if (val.isEmpty()) {
            binding.userProfileName.setError("Field Cannot Be Empty");
            return false;
        } else if (!m.matches()) {
            binding.userProfileName.setError("No White spaces are allowed!");
            return false;
        } else {
            binding.userProfileName.setError(null);
            binding.userProfileName.setErrorEnabled(false);
            return true;
        }
    }

}