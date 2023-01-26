package com.example.socion.Dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.socion.Calling.Common.Activity.CreateOrJoinActivity;
import com.example.socion.Calling.OneToOneCall.OneToOneCallActivity;
import com.example.socion.HelpingClasses.Common;
import com.example.socion.HelpingClasses.CommonVar;
import com.example.socion.HelpingClasses.User;
import com.example.socion.R;
import com.example.socion.databinding.ActivityDashboardBinding;
import com.example.socion.databinding.ActivityForgotBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DashboardActivity extends AppCompatActivity {
    ActivityDashboardBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    User user;

    String url = CommonVar.URL_CONVERT;
    String urlVideo = CommonVar.URL_CONVERT_VIDEO;

    public static String emotionVideo;
    public static String emotionAudio;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog2;
    String emoteAudio;
    String emoteVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        progressDialog = new ProgressDialog(DashboardActivity.this);
        //region Fetching Current User Data
        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        String userID = mAuth.getCurrentUser().getUid();

        Log.e("Dashboard", userID);

        firebaseFirestore.collection("USERS").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {

                    if (error != null) {
                        Log.i("appCheck", "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        changeData(value);
                    } else {
                        Log.i("appCheck", "Current data: null ");
                    }

                }
            }
        });
        //endregion

        initCarosel();

        binding.createGroupCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, CreateOrJoinActivity.class);
                intent.putExtra("name", Common.u_name);
                intent.putExtra("meeting", "Group Call");
                intent.putExtra("meetingMode", "create");
                startActivity(intent);
            }
        });

        binding.joinGroupCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, CreateOrJoinActivity.class);
                intent.putExtra("name", Common.u_name);
                intent.putExtra("meeting", "Group Call");
                intent.putExtra("meetingMode", "join");
                startActivity(intent);
            }
        });

        binding.createOneToOneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, CreateOrJoinActivity.class);
                intent.putExtra("name", Common.u_name);
                intent.putExtra("meeting", "One to One Meeting");
                intent.putExtra("meetingMode", "create");
                startActivity(intent);
            }
        });

        binding.joinOneToOneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, CreateOrJoinActivity.class);
                intent.putExtra("name", Common.u_name);
                intent.putExtra("meeting", "One to One Meeting");
                intent.putExtra("meetingMode", "join");
                startActivity(intent);
            }
        });

        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        //region Function Will get value from model to be predicted
        if (getIntent().getExtras() != null) {
            emoteAudio = getIntent().getStringExtra("audioURL");
            //emoteVideo = getIntent().getStringExtra("videoURL");
            getData(emoteAudio);
        }else{
            displayDialog("Angry");
        }
    }

    private String convertToCamelCase(String str) {
        String s = "str";
        String s1 = str.substring(0, 1).toUpperCase();
        String s2 = str.substring(1);
        String res = str.substring(0, 1).toUpperCase() + str.substring(1);
        return res;
    }

    private void initCarosel() {
        firebaseFirestore.collection("SLIDES").orderBy("index", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                binding.carousel.addData(new CarouselItem(documentSnapshot.getString("url")));
                            }
                        }
                    }
                });

    }


    public void displayDialog(String emote) {
        progressDialog.dismiss();

        Dialog dialog = new Dialog(this, R.style.DialogStyle);
        dialog.setContentView(R.layout.alert_emotion);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.button_round);

        LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.emotion);
        TextView desc = dialog.findViewById(R.id.emotion_text);

        desc.setText("Are You Feeling " + emote);

        Button yes = (Button) dialog.findViewById(R.id.emotion_yes);
        Button no = (Button) dialog.findViewById(R.id.emotion_no);


        if(!((Activity)DashboardActivity.this).isFinishing())
        {
            //show dialog
            dialog.show();
        }

        if (emote.equals("Happy")) {
            lottieAnimationView.setAnimation(R.raw.smile);
        } else if (emote.equals("Sad")) {
            lottieAnimationView.setAnimation(R.raw.sad_emo);
        } else if (emote.equals("Angry")) {
            lottieAnimationView.setAnimation(R.raw.angry_emo);
        } else {
            lottieAnimationView.setAnimation(R.raw.neutral_emo);
        }

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, RecommendationActivity.class);
                intent.putExtra("emotion", emote);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, RecommendationActivity.class);
                intent.putExtra("emotion", "neutral");
                startActivity(intent);
                dialog.dismiss();
            }
        });

    }

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
        binding.userName.setText("Hi, " + Common.u_name);
        Glide.with(getApplicationContext())
                .load(Common.u_profile)
                .centerCrop()
                .placeholder(R.drawable.avatar)
                .into(binding.userImage);
        //endregion

    }

    String emote;

    private void getData(String data) {
        RequestQueue queue = Volley.newRequestQueue(DashboardActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    emotionAudio = jsonResponse.getString("emote");
                    displayDialog(emotionAudio);
//                    if (!TextUtils.isEmpty(emoteVideo)) {
//                        getDataTwo(emoteVideo);
//                    }
                    Toast.makeText(getApplicationContext(), "Audio Processed Successfully!", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayDialog("Neutral");
                Toast.makeText(DashboardActivity.this, "getData_Failed", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("url", data);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }

    private void getDataTwo(String data) {
        RequestQueue queue = Volley.newRequestQueue(DashboardActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, urlVideo, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                emotionVideo = response;
                if (emotionAudio.equals(emotionVideo)) {
                    Log.i("OneToOne", emotionVideo + "  audio " + emotionAudio);
                    emote = emotionVideo;
                } else {
                    Log.i("OneToOne: getDataTwo ", "  audio " + emotionAudio);
                    emote = emotionAudio;
                }
                displayDialog(emote);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayDialog(emotionAudio);
                Toast.makeText(DashboardActivity.this, "getDataTwo_Failed", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("video", data);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }


}