package com.example.socion.Login;

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

import com.example.socion.Dashboard.DashboardActivity;
import com.example.socion.HelpingClasses.Common;
import com.example.socion.R;
import com.example.socion.databinding.ActivitySetupProfileBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class SetupProfileActivity extends AppCompatActivity {

    ActivitySetupProfileBinding binding;
    String uid;
    String userName;
    String userPhoneNumber;
    String userPassword;
    String DownloadImageUrl;
    private Uri imageUri;
    StorageReference UserImagesRef;
    ActivityResultLauncher<Intent> startForProfileImageResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getDataFromIntent();
        //Displaying Previous Intent information

        binding.registerName.setText(userName);
        binding.registerNumber.setText(userPhoneNumber);

        setUserImage();

        binding.finishSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isNetworkAvailable(SetupProfileActivity.this) && imageUri != null) {
                    StoreProductImageInformation();
                } else {
                    Toast.makeText(SetupProfileActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SetupProfileActivity.this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SetupProfileActivity.this, "Task Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(SetupProfileActivity.this)
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

    private void getDataFromIntent() {
        UserImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        userName = getIntent().getStringExtra("name");
        userPassword = getIntent().getStringExtra("password");
        userPhoneNumber = getIntent().getStringExtra("phone");
        uid = getIntent().getStringExtra("uid");
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
                Toast.makeText(SetupProfileActivity.this, "Error " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SetupProfileActivity.this, "Product Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                //if image is uploaded it will get that link of image to be stored in database

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
                            SaveProductInformationToDB();

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
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("uid", uid);
        userInfo.put("name", userName);
        userInfo.put("phone", userPhoneNumber);
        userInfo.put("password", userPassword);
        userInfo.put("profile", DownloadImageUrl);

        FirebaseFirestore.getInstance().collection("USERS").document(uid).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                startActivity(new Intent(SetupProfileActivity.this, DashboardActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }


}