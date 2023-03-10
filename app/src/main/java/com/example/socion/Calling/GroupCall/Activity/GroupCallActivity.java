package com.example.socion.Calling.GroupCall.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.socion.Calling.Common.Activity.CreateOrJoinActivity;
import com.example.socion.Calling.Common.Adapter.AudioDeviceListAdapter;
import com.example.socion.Calling.Common.Adapter.LeaveOptionListAdapter;
import com.example.socion.Calling.Common.Adapter.MessageAdapter;
import com.example.socion.Calling.Common.Adapter.MoreOptionsListAdapter;
import com.example.socion.Calling.Common.Adapter.ParticipantListAdapter;
import com.example.socion.Calling.Common.Listener.ResponseListener;
import com.example.socion.Calling.Common.Modal.ListItem;
import com.example.socion.Calling.Common.Roboto_font;
import com.example.socion.Calling.Common.Utils.HelperClass;
import com.example.socion.Calling.Common.Utils.NetworkUtils;
import com.example.socion.Calling.GroupCall.Adapter.ParticipantViewAdapter;
import com.example.socion.Calling.GroupCall.Utils.ParticipantState;
import com.example.socion.Calling.OneToOneCall.OneToOneCallActivity;
import com.example.socion.Dashboard.DashboardActivity;
import com.example.socion.HelpingClasses.CommonVar;
import com.example.socion.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbisoft.hbrecorder.HBRecorder;
import com.hbisoft.hbrecorder.HBRecorderListener;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import live.videosdk.rtc.android.CustomStreamTrack;
import live.videosdk.rtc.android.Meeting;
import live.videosdk.rtc.android.Participant;
import live.videosdk.rtc.android.Stream;
import live.videosdk.rtc.android.VideoSDK;
import live.videosdk.rtc.android.lib.AppRTCAudioManager;
import live.videosdk.rtc.android.lib.JsonUtils;
import live.videosdk.rtc.android.lib.PeerConnectionUtils;
import live.videosdk.rtc.android.lib.PubSubMessage;
import live.videosdk.rtc.android.listeners.MeetingEventListener;
import live.videosdk.rtc.android.listeners.MicRequestListener;
import live.videosdk.rtc.android.listeners.ParticipantEventListener;
import live.videosdk.rtc.android.listeners.PubSubMessageListener;
import live.videosdk.rtc.android.listeners.WebcamRequestListener;
import live.videosdk.rtc.android.model.PubSubPublishOptions;


public class GroupCallActivity extends AppCompatActivity implements HBRecorderListener {

    private static final int SCREEN_RECORD_REQUEST_CODE = 100;
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 101;
    private static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = 102;

    private static Meeting meeting;
    private FloatingActionButton btnWebcam;
    private ImageButton btnMic, btnAudioSelection, btnSwitchCameraMode;
    private FloatingActionButton btnLeave, btnChat, btnMore;

    private LinearLayout micLayout;
    ArrayList<Participant> participants;
    private SurfaceViewRenderer svrShare;
    private FrameLayout shareLayout;

    private boolean micEnabled = true;
    private boolean webcamEnabled = true;
    private boolean recording = false;
    private boolean localScreenShare = false;
    private static String token;
    private Snackbar recordingStatusSnackbar;


    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;

    private boolean screenshareEnabled = false;
    private BottomSheetDialog bottomSheetDialog;
    private String selectedAudioDeviceName;

    private EditText etmessage;
    private MessageAdapter messageAdapter;
    private PubSubMessageListener pubSubMessageListener;
    private ViewPager2 viewPager2;
    private ParticipantViewAdapter viewAdapter;
    private int meetingSeconds;
    private TextView txtMeetingTime;
    private Button btnStopScreenShare;

    int clickCount = 0;
    long startTime;
    static final int MAX_DURATION = 500;
    boolean fullScreen = false;
    View.OnTouchListener onTouchListener;
    private Snackbar screenShareParticipantNameSnackbar;
    private Runnable runnable;
    final Handler handler = new Handler();
    private PubSubMessageListener chatListener;
    private PubSubMessageListener raiseHandListener;

    private MediaRecorder recorder;
    private String mFilename = null;
    private String mFilenameVideo = null;

    ProgressDialog progressDialog;
    StorageReference storageReference;
    public static final String TAG = "OneToOne";
    public static boolean audioProcessed;
    public static boolean videoProcessed;
    boolean isVideoOn = false;

    String url = CommonVar.URL_CONVERT;
    String urlVideo = CommonVar.URL_CONVERT_VIDEO;

    public static String emotionVideo;
    public static String emotionAudio;
    HBRecorder hbRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_call);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hbRecorder = new HBRecorder(this, this);
        hbRecorder.isAudioEnabled(false);
        hbRecorder.setMaxDuration(60);
        hbRecorder.setVideoFrameRate(1);
        hbRecorder.setScreenDimensions(500, 400);
        hbRecorder.setVideoEncoder("H264");

        String music = "recording";
        mFilename = getApplication().getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + music + ".m4a";

        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        //
        Toolbar toolbar = findViewById(R.id.material_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //
        btnLeave = findViewById(R.id.btnLeave);
        btnChat = findViewById(R.id.btnChat);
        btnMore = findViewById(R.id.btnMore);
        btnSwitchCameraMode = findViewById(R.id.btnSwitchCameraMode);

        micLayout = findViewById(R.id.micLayout);
        btnMic = findViewById(R.id.btnMic);
        btnWebcam = findViewById(R.id.btnWebcam);
        btnAudioSelection = findViewById(R.id.btnAudioSelection);
        txtMeetingTime = findViewById(R.id.txtMeetingTime);
        btnStopScreenShare = findViewById(R.id.btnStopScreenShare);


        viewPager2 = findViewById(R.id.view_pager_video_grid);
        shareLayout = findViewById(R.id.shareLayout);
        svrShare = findViewById(R.id.svrShare);
        svrShare.init(PeerConnectionUtils.getEglContext(), null);

        token = getIntent().getStringExtra("token");
        final String meetingId = getIntent().getStringExtra("meetingId");
        micEnabled = getIntent().getBooleanExtra("micEnabled", true);
        webcamEnabled = getIntent().getBooleanExtra("webcamEnabled", true);

        String localParticipantName = getIntent().getStringExtra("participantName");
        if (localParticipantName == null) {
            localParticipantName = "John Doe";
        }

        // pass the token generated from api server
        VideoSDK.config(token);

        Map<String, CustomStreamTrack> customTracks = new HashMap<>();

        CustomStreamTrack videoCustomTrack = VideoSDK.createCameraVideoTrack("h720p_w960p", "front", CustomStreamTrack.VideoMode.TEXT, this);
        customTracks.put("video", videoCustomTrack);

        JSONObject noiseConfig = new JSONObject();
        JsonUtils.jsonPut(noiseConfig, "acousticEchoCancellation", true);
        JsonUtils.jsonPut(noiseConfig, "noiseSuppression", true);
        JsonUtils.jsonPut(noiseConfig, "autoGainControl", true);

        CustomStreamTrack audioCustomTrack = VideoSDK.createAudioTrack("high_quality", noiseConfig, this);
        customTracks.put("mic", audioCustomTrack);

        // create a new meeting instance
        meeting = VideoSDK.initMeeting(
                GroupCallActivity.this, meetingId, localParticipantName,
                micEnabled, webcamEnabled, null, customTracks
        );

        //
        TextView textMeetingId = findViewById(R.id.txtMeetingId);
        textMeetingId.setText(meetingId);

        meeting.addEventListener(meetingEventListener);

        //show Progress
        HelperClass.showProgress(getWindow().getDecorView().getRootView());

        //
        checkPermissions();

        // Actions
        setActionListeners();

        setAudioDeviceListeners();

        ((ImageButton) findViewById(R.id.btnCopyContent)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyTextToClipboard(meetingId);
            }
        });

        btnAudioSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAudioInputDialog();
            }
        });

        btnStopScreenShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (localScreenShare) {
                    meeting.disableScreenShare();
                }
            }
        });

        recordingStatusSnackbar = Snackbar.make(findViewById(R.id.mainLayout), "Recording will be started in few moments",
                Snackbar.LENGTH_INDEFINITE);
        HelperClass.setSnackBarStyle(recordingStatusSnackbar.getView(), 0);
        recordingStatusSnackbar.setGestureInsetBottomIgnored(true);

        viewAdapter = new ParticipantViewAdapter(GroupCallActivity.this, meeting);

        onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_UP:

                        clickCount++;

                        if (clickCount == 1) {
                            startTime = System.currentTimeMillis();
                        } else if (clickCount == 2) {
                            long duration = System.currentTimeMillis() - startTime;
                            if (duration <= MAX_DURATION) {
                                if (fullScreen) {
                                    toolbar.setVisibility(View.VISIBLE);
                                    for (int i = 0; i < toolbar.getChildCount(); i++) {
                                        toolbar.getChildAt(i).setVisibility(View.VISIBLE);
                                    }

                                    Toolbar.LayoutParams params = new Toolbar.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    );
                                    params.setMargins(22, 10, 0, 0);
                                    findViewById(R.id.meetingLayout).setLayoutParams(params);

                                    shareLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(420, GroupCallActivity.this)));
                                    ((LinearLayout) findViewById(R.id.localScreenShareView)).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(420, GroupCallActivity.this)));

                                    TranslateAnimation toolbarAnimation = new TranslateAnimation(
                                            0,
                                            0,
                                            0,
                                            10);
                                    toolbarAnimation.setDuration(500);
                                    toolbarAnimation.setFillAfter(true);
                                    toolbar.startAnimation(toolbarAnimation);

                                    BottomAppBar bottomAppBar = findViewById(R.id.bottomAppbar);
                                    bottomAppBar.setVisibility(View.VISIBLE);
                                    for (int i = 0; i < bottomAppBar.getChildCount(); i++) {
                                        bottomAppBar.getChildAt(i).setVisibility(View.VISIBLE);
                                    }

                                    TranslateAnimation animate = new TranslateAnimation(
                                            0,
                                            0,
                                            findViewById(R.id.bottomAppbar).getHeight(),
                                            0);
                                    animate.setDuration(300);
                                    animate.setFillAfter(true);
                                    findViewById(R.id.bottomAppbar).startAnimation(animate);
                                } else {
                                    toolbar.setVisibility(View.GONE);
                                    for (int i = 0; i < toolbar.getChildCount(); i++) {
                                        toolbar.getChildAt(i).setVisibility(View.GONE);
                                    }

                                    shareLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(500, GroupCallActivity.this)));

                                    ((LinearLayout) findViewById(R.id.localScreenShareView)).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(500, GroupCallActivity.this)));

                                    TranslateAnimation toolbarAnimation = new TranslateAnimation(
                                            0,
                                            0,
                                            0,
                                            10);
                                    toolbarAnimation.setDuration(500);
                                    toolbarAnimation.setFillAfter(true);
                                    toolbar.startAnimation(toolbarAnimation);

                                    BottomAppBar bottomAppBar = findViewById(R.id.bottomAppbar);
                                    bottomAppBar.setVisibility(View.GONE);
                                    for (int i = 0; i < bottomAppBar.getChildCount(); i++) {
                                        bottomAppBar.getChildAt(i).setVisibility(View.GONE);
                                    }

                                    TranslateAnimation animate = new TranslateAnimation(
                                            0,
                                            0,
                                            0,
                                            findViewById(R.id.bottomAppbar).getHeight());
                                    animate.setDuration(400);
                                    animate.setFillAfter(true);
                                    findViewById(R.id.bottomAppbar).startAnimation(animate);
                                }
                                fullScreen = !fullScreen;
                                clickCount = 0;
                            } else {
                                clickCount = 1;
                                startTime = System.currentTimeMillis();
                            }
                            break;
                        }
                }

                return true;
            }
        };

        findViewById(R.id.participants_Layout).setOnTouchListener(onTouchListener);
    }

    public View.OnTouchListener getOnTouchListener() {
        return onTouchListener;
    }

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void toggleMicIcon() {
        if (micEnabled) {
            btnMic.setImageResource(R.drawable.ic_mic_on);
            btnAudioSelection.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
            micLayout.setBackground(ContextCompat.getDrawable(GroupCallActivity.this, R.drawable.layout_selected));
        } else {
            btnMic.setImageResource(R.drawable.ic_mic_off_24);
            btnAudioSelection.setImageResource(R.drawable.ic_baseline_arrow_drop_down);
            micLayout.setBackgroundColor(Color.WHITE);
            micLayout.setBackground(ContextCompat.getDrawable(GroupCallActivity.this, R.drawable.layout_nonselected));
        }
    }

    @SuppressLint("ResourceType")
    private void toggleWebcamIcon() {
        if (webcamEnabled) {
            btnWebcam.setImageResource(R.drawable.ic_video_camera);
            btnWebcam.setColorFilter(Color.WHITE);
            Drawable buttonDrawable = btnWebcam.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            if (buttonDrawable != null) DrawableCompat.setTint(buttonDrawable, Color.TRANSPARENT);
            btnWebcam.setBackground(buttonDrawable);

        } else {
            btnWebcam.setImageResource(R.drawable.ic_video_camera_off);
            btnWebcam.setColorFilter(Color.BLACK);
            Drawable buttonDrawable = btnWebcam.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            if (buttonDrawable != null) DrawableCompat.setTint(buttonDrawable, Color.WHITE);
            btnWebcam.setBackground(buttonDrawable);
        }
    }


    private final MeetingEventListener meetingEventListener = new MeetingEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onMeetingJoined() {
            if (meeting != null) {
                //hide progress when meetingJoined
                HelperClass.hideProgress(getWindow().getDecorView().getRootView());
                startRecording();
                toggleMicIcon();
                toggleWebcamIcon();

                setLocalListeners();

                new NetworkUtils(GroupCallActivity.this).fetchMeetingTime(meeting.getMeetingId(), token, new ResponseListener() {
                    @Override
                    public void onResponse(String meetingId) {
                    }

                    @Override
                    public void onMeetingTimeChanged(int meetingTime) {
                        meetingSeconds = meetingTime;
                        showMeetingTime();
                    }
                });

                viewPager2.setOffscreenPageLimit(1);
                viewPager2.setAdapter(viewAdapter);


                raiseHandListener = new PubSubMessageListener() {
                    @Override
                    public void onMessageReceived(PubSubMessage pubSubMessage) {
                        View parentLayout = findViewById(android.R.id.content);
                        SpannableStringBuilder builderTextLeft = new SpannableStringBuilder();
                        if (pubSubMessage.getSenderId().equals(meeting.getLocalParticipant().getId())) {
                            builderTextLeft.append("   You raised hand");
                        } else {
                            builderTextLeft.append("   " + pubSubMessage.getSenderName() + " raised hand  ");
                        }
                        Drawable drawable = getResources().getDrawable(R.drawable.ic_raise_hand);
                        drawable.setBounds(0, 0, 50, 65);
                        builderTextLeft.setSpan(new ImageSpan(drawable), 0, 1, 0);
                        Snackbar snackbar = Snackbar.make(parentLayout, builderTextLeft,
                                Snackbar.LENGTH_SHORT);
                        HelperClass.setSnackBarStyle(snackbar.getView(), 0);
                        snackbar.setGestureInsetBottomIgnored(true);
                        snackbar.getView().setOnClickListener(view -> snackbar.dismiss());
                        snackbar.show();
                    }
                };

                // notify user for raise hand
                meeting.pubSub.subscribe("RAISE_HAND", raiseHandListener);


                chatListener = new PubSubMessageListener() {
                    @Override
                    public void onMessageReceived(PubSubMessage pubSubMessage) {
                        if (!pubSubMessage.getSenderId().equals(meeting.getLocalParticipant().getId())) {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar snackbar =
                                    Snackbar.make(parentLayout, pubSubMessage.getSenderName() + " says: " +
                                                    pubSubMessage.getMessage(), Snackbar.LENGTH_SHORT)
                                            .setDuration(2000);
                            View snackbarView = snackbar.getView();
                            HelperClass.setSnackBarStyle(snackbarView, 0);
                            snackbar.getView().setOnClickListener(view -> snackbar.dismiss());
                            snackbar.show();
                        }
                    }
                };
                // notify user of any new messages
                meeting.pubSub.subscribe("CHAT", chatListener);

                //terminate meeting in 10 minutes
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isDestroyed()) {
                            AlertDialog alertDialog = new MaterialAlertDialogBuilder(GroupCallActivity.this, R.style.AlertDialogCustom).create();
                            alertDialog.setCanceledOnTouchOutside(false);

                            LayoutInflater inflater = GroupCallActivity.this.getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.alert_dialog_layout, null);
                            alertDialog.setView(dialogView);

                            TextView title = (TextView) dialogView.findViewById(R.id.title);
                            title.setText("Meeting Left");
                            TextView message = (TextView) dialogView.findViewById(R.id.message);
                            message.setText("Demo app limits meeting to 10 Minutes");

                            Button positiveButton = dialogView.findViewById(R.id.positiveBtn);
                            positiveButton.setText("Ok");
                            positiveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!isDestroyed()) {
                                        ParticipantState.destroy();
                                        unSubscribeTopics();
                                        meeting.leave();
                                    }
                                    alertDialog.dismiss();
                                }
                            });

                            Button negativeButton = dialogView.findViewById(R.id.negativeBtn);
                            negativeButton.setVisibility(View.GONE);

                            alertDialog.show();
                        }

                    }
                }, 600000);
            }
        }


        private void startRecording() {

            startRecordingScreen();

            //below is audio recording and upper is video recording
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(mFilename);

            try {
                recorder.prepare();
                recorder.start();
            } catch (IOException e) {
                Log.e("MIC", "Prepare Failed: " + e.getMessage());
            }
        }

        private void stopRecording() {

            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
                uploadAudio();

                if (hbRecorder.isBusyRecording()) {
                    hbRecorder.stopScreenRecording();
                    uploadVideo();
                }

            }

        }

        String DownloadAudioUrl = "";
        String DownloadVideoUrl = "";

        private void uploadAudio() {
            progressDialog.setMessage("Uploading Audio");
            progressDialog.show();
            //Unique Name of File
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
            String saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            String saveCurrentTime = currentTime.format(calendar.getTime());

            String audioName = saveCurrentDate + saveCurrentTime + ".m4a";

            StorageReference filepath = storageReference.child("Audio").child(audioName);
            Uri uri = Uri.fromFile(new File(mFilename));
            Log.i("One", "Audio URI " + uri);
            final UploadTask uploadTask = filepath.putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            DownloadAudioUrl = filepath.getDownloadUrl().toString();
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                DownloadAudioUrl = task.getResult().toString();
                                Log.i("OneToOne","Audio Link "+DownloadAudioUrl);
                                Toast.makeText(getApplicationContext(), "Audio Processed Successfully!", Toast.LENGTH_SHORT).show();
                                getData(DownloadAudioUrl);
                                audioProcessed = true;
                            }
                        }
                    });

                }
            });

        }

        private void uploadVideo() {
            progressDialog.setMessage("Uploading Video");
            progressDialog.show();

            //Unique Name of File
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
            String saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            String saveCurrentTime = currentTime.format(calendar.getTime());

            String videoName = saveCurrentDate + saveCurrentTime + ".mp4";

            StorageReference filepath = storageReference.child("Video").child(videoName);
            Uri uri = Uri.fromFile(new File(hbRecorder.getFilePath()));

            Log.i("One", "Video URI " + uri);

            final UploadTask uploadTask = filepath.putFile(uri);

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    double progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + progress);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            DownloadVideoUrl = filepath.getDownloadUrl().toString();
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                DownloadVideoUrl = task.getResult().toString();
                                Log.e("OneToOne","Video Link "+ DownloadVideoUrl);
                                //getDataTwo(DownloadVideoUrl);
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            });

        }

        private void getData(String data) {
            RequestQueue queue = Volley.newRequestQueue(GroupCallActivity.this);
            StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        emotionAudio = jsonResponse.getString("emote");
                        Toast.makeText(getApplicationContext(), "Audio Processed Successfully!", Toast.LENGTH_SHORT).show();
                        getDataTwo(urlVideo); //Now Passing Value to Video Frame

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("OneToOne", "Error " + error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("url", data);
                    return params;
                }
            };
            request.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

            queue.add(request);
            //When Audio is finished
        }



        private void getDataTwo(String data) {
            RequestQueue queue = Volley.newRequestQueue(GroupCallActivity.this);
            StringRequest request = new StringRequest(Request.Method.POST, urlVideo, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    emotionVideo = response;
                    String emote;
                    if(emotionAudio.equals(emotionVideo)){
                        Log.i("OneToOne",emotionVideo+"  audio "+emotionAudio);
                        emote = emotionVideo;
                    }else{
                        emote = emotionAudio;
                    }
                    Toast.makeText(getApplicationContext(), "Video Processed Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intents = new Intent(GroupCallActivity.this, DashboardActivity.class);
                    intents.putExtra("emote",emote);
                    intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intents);
                    finish();
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("err", "Error " + error);
                    Toast.makeText(getApplicationContext(), "Audio Processed Only!", Toast.LENGTH_SHORT).show();
                    Intent intents = new Intent(GroupCallActivity.this, DashboardActivity.class);
                    intents.putExtra("emote",emotionAudio);
                    intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intents);
                    finish();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("video", data);
                    return params;
                }
            };
            queue.add(request);
        }


        @Override
        public void onMeetingLeft() {
            handler.removeCallbacks(runnable);
            if (!isDestroyed()) {
                stopRecording();

            }
        }

        @Override
        public void onPresenterChanged(String participantId) {
            updatePresenter(participantId);
        }

        @Override
        public void onRecordingStarted() {
            recording = true;

            recordingStatusSnackbar.dismiss();
            (findViewById(R.id.recordingLottie)).setVisibility(View.VISIBLE);
            Toast.makeText(GroupCallActivity.this, "Recording started",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRecordingStopped() {
            recording = false;

            (findViewById(R.id.recordingLottie)).setVisibility(View.GONE);

            Toast.makeText(GroupCallActivity.this, "Recording stopped",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onExternalCallStarted() {
            Toast.makeText(GroupCallActivity.this, "onExternalCallStarted", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(JSONObject error) {
            try {
                JSONObject errorCodes = VideoSDK.getErrorCodes();
                int code = error.getInt("code");
                if (code == errorCodes.getInt("PREV_RECORDING_PROCESSING")) {
                    recordingStatusSnackbar.dismiss();
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.mainLayout), "Please try again after sometime",
                            Snackbar.LENGTH_LONG);
                    HelperClass.setSnackBarStyle(snackbar.getView(), 0);
                    snackbar.getView().setOnClickListener(view -> snackbar.dismiss());
                    snackbar.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSpeakerChanged(String participantId) {

        }

        @Override
        public void onMeetingStateChanged(String state) {
            if (state == "FAILED") {
                View parentLayout = findViewById(android.R.id.content);
                SpannableStringBuilder builderTextLeft = new SpannableStringBuilder();
                builderTextLeft.append("   Call disconnected. Reconnecting...");
                builderTextLeft.setSpan(new ImageSpan(GroupCallActivity.this, R.drawable.ic_call_disconnected), 0, 1, 0);
                Snackbar snackbar = Snackbar.make(parentLayout, builderTextLeft, Snackbar.LENGTH_LONG);
                HelperClass.setSnackBarStyle(snackbar.getView(), getResources().getColor(R.color.md_red_400));
                snackbar.getView().setOnClickListener(view -> snackbar.dismiss());
                snackbar.show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (handler.hasCallbacks(runnable))
                        handler.removeCallbacks(runnable);
                }
            }
        }

        @Override
        public void onMicRequested(String participantId, MicRequestListener listener) {
            showMicRequestDialog(listener);
        }

        @Override
        public void onWebcamRequested(String participantId, WebcamRequestListener listener) {
            showWebcamRequestDialog(listener);
        }
    };


    private void setLocalListeners() {

        meeting.getLocalParticipant().addEventListener(new ParticipantEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onStreamEnabled(Stream stream) {
                if (stream.getKind().equalsIgnoreCase("video")) {
                    webcamEnabled = true;
                    toggleWebcamIcon();
                } else if (stream.getKind().equalsIgnoreCase("audio")) {
                    micEnabled = true;
                    toggleMicIcon();
                } else if (stream.getKind().equalsIgnoreCase("share")) {

                    findViewById(R.id.localScreenShareView).setVisibility(View.VISIBLE);
                    screenShareParticipantNameSnackbar = Snackbar.make(findViewById(R.id.mainLayout), "You started presenting",
                            Snackbar.LENGTH_SHORT);
                    HelperClass.setSnackBarStyle(screenShareParticipantNameSnackbar.getView(), 0);
                    screenShareParticipantNameSnackbar.setGestureInsetBottomIgnored(true);
                    screenShareParticipantNameSnackbar.getView().setOnClickListener(view -> screenShareParticipantNameSnackbar.dismiss());
                    screenShareParticipantNameSnackbar.show();

                    localScreenShare = true;
                    screenshareEnabled = true;
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onStreamDisabled(Stream stream) {
                if (stream.getKind().equalsIgnoreCase("video")) {
                    webcamEnabled = false;
                    toggleWebcamIcon();
                } else if (stream.getKind().equalsIgnoreCase("audio")) {
                    micEnabled = false;
                    toggleMicIcon();
                } else if (stream.getKind().equalsIgnoreCase("share")) {

                    findViewById(R.id.localScreenShareView).setVisibility(View.GONE);

                    localScreenShare = false;
                    screenshareEnabled = false;
                }
            }
        });
    }

    @TargetApi(21)
    private void askPermissionForScreenShare() {
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getApplication().getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE)
            return;
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(GroupCallActivity.this, "You didn't give permission to capture the screen.", Toast.LENGTH_SHORT).show();
            localScreenShare = false;
            return;
        }

        meeting.enableScreenShare(data);
    }

    private final PermissionHandler permissionHandler = new PermissionHandler() {
        @Override
        public void onGranted() {
            if (meeting != null) meeting.join();
        }
    };

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE
        };
        String rationale = "Please provide permissions";
        Permissions.Options options =
                new Permissions.Options().setRationaleDialogTitle("Info").setSettingsDialogTitle("Warning");
        Permissions.check(this, permissions, rationale, options, permissionHandler);
    }

    private void setAudioDeviceListeners() {

        meeting.setAudioDeviceChangeListener(new AppRTCAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(AppRTCAudioManager.AudioDevice selectedAudioDevice, Set<AppRTCAudioManager.AudioDevice> availableAudioDevices) {
                selectedAudioDeviceName = selectedAudioDevice.toString();
            }
        });
    }

    private void copyTextToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied text", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(GroupCallActivity.this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    private void toggleMic() {
        if (micEnabled) {
            meeting.muteMic();
        } else {
            JSONObject noiseConfig = new JSONObject();
            JsonUtils.jsonPut(noiseConfig, "acousticEchoCancellation", true);
            JsonUtils.jsonPut(noiseConfig, "noiseSuppression", true);
            JsonUtils.jsonPut(noiseConfig, "autoGainControl", true);

            CustomStreamTrack audioCustomTrack = VideoSDK.createAudioTrack("high_quality", noiseConfig, this);

            meeting.unmuteMic(audioCustomTrack);
        }
    }

    private void toggleWebCam() {
        if (webcamEnabled) {
            meeting.disableWebcam();
        } else {
            CustomStreamTrack videoCustomTrack = VideoSDK.createCameraVideoTrack("h720p_w960p", "front", CustomStreamTrack.VideoMode.DETAIL, this);
            meeting.enableWebcam(videoCustomTrack);
        }
    }

    private void setActionListeners() {
        // Toggle mic
        micLayout.setOnClickListener(view -> {
            toggleMic();
        });

        btnMic.setOnClickListener(view -> {
            toggleMic();
        });

        // Toggle webcam
        btnWebcam.setOnClickListener(view -> {
            toggleWebCam();
        });

        // Leave meeting
        btnLeave.setOnClickListener(view -> {
            showLeaveOrEndDialog();
        });

        btnMore.setOnClickListener(v -> showMoreOptionsDialog());

        btnSwitchCameraMode.setOnClickListener(view -> {
            meeting.changeWebcam();
        });

        // Chat
        btnChat.setOnClickListener(view -> {
            if (meeting != null) {
                openChat();
            }
        });

    }

    private void toggleScreenSharing() {
        if (!screenshareEnabled) {
            if (!localScreenShare) {
                askPermissionForScreenShare();
            }
            localScreenShare = !localScreenShare;
        } else {
            if (localScreenShare) {
                meeting.disableScreenShare();
            } else {
                Toast.makeText(this, "You can't share your screen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updatePresenter(String participantId) {
        if (participantId == null) {
            svrShare.clearImage();
            svrShare.setVisibility(View.GONE);
            shareLayout.setVisibility(View.GONE);
            screenshareEnabled = false;
            return;
        } else {
            screenshareEnabled = true;
        }

        // find participant
        Participant participant = meeting.getParticipants().get(participantId);
        if (participant == null) return;

        // find share stream in participant
        Stream shareStream = null;

        for (Stream stream : participant.getStreams().values()) {
            if (stream.getKind().equals("share")) {
                shareStream = stream;
                break;
            }
        }

        if (shareStream == null) return;
        ((TextView) findViewById(R.id.tvScreenShareParticipantName)).setText(participant.getDisplayName() + " is presenting");
        findViewById(R.id.tvScreenShareParticipantName).setVisibility(View.VISIBLE);

        // display share video
        shareLayout.setVisibility(View.VISIBLE);
        svrShare.setVisibility(View.VISIBLE);
        svrShare.setZOrderMediaOverlay(true);
        svrShare.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

        VideoTrack videoTrack = (VideoTrack) shareStream.getTrack();
        videoTrack.addSink(svrShare);

        screenShareParticipantNameSnackbar = Snackbar.make(findViewById(R.id.mainLayout), participant.getDisplayName() + " started presenting",
                Snackbar.LENGTH_SHORT);
        HelperClass.setSnackBarStyle(screenShareParticipantNameSnackbar.getView(), 0);
        screenShareParticipantNameSnackbar.setGestureInsetBottomIgnored(true);
        screenShareParticipantNameSnackbar.getView().setOnClickListener(view -> screenShareParticipantNameSnackbar.dismiss());
        screenShareParticipantNameSnackbar.show();

        // listen for share stop event
        participant.addEventListener(new ParticipantEventListener() {
            @Override
            public void onStreamDisabled(Stream stream) {
                if (stream.getKind().equals("share")) {
                    VideoTrack track = (VideoTrack) stream.getTrack();
                    if (track != null) track.removeSink(svrShare);

                    svrShare.clearImage();
                    svrShare.setVisibility(View.GONE);
                    shareLayout.setVisibility(View.GONE);
                    findViewById(R.id.tvScreenShareParticipantName).setVisibility(View.GONE);
                    localScreenShare = false;
                }
            }
        });
    }

    private void showLeaveOrEndDialog() {
        ArrayList<ListItem> OptionsArrayList = new ArrayList<>();
        ListItem leaveMeeting = new ListItem("Leave", "Only you will leave the call", AppCompatResources.getDrawable(GroupCallActivity.this, R.drawable.ic_leave));
        ListItem endMeeting = new ListItem("End", "End call for all the participants", AppCompatResources.getDrawable(GroupCallActivity.this, R.drawable.ic_end_meeting));

        OptionsArrayList.add(leaveMeeting);
        OptionsArrayList.add(endMeeting);

        ArrayAdapter arrayAdapter = new LeaveOptionListAdapter(GroupCallActivity.this, R.layout.leave_options_list_layout, OptionsArrayList);
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(GroupCallActivity.this, R.style.AlertDialogCustom)
                .setAdapter(arrayAdapter, (dialog, which) -> {
                    switch (which) {
                        case 0: {
                            viewPager2.setAdapter(null);
                            ParticipantState.destroy();
                            unSubscribeTopics();
                            meeting.leave();
                            break;
                        }
                        case 1: {
                            viewPager2.setAdapter(null);
                            ParticipantState.destroy();
                            unSubscribeTopics();
                            meeting.end();
                            break;
                        }
                    }
                });

        AlertDialog alertDialog = materialAlertDialogBuilder.create();

        ListView listView = alertDialog.getListView();
        listView.setDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.md_grey_200))); // set color
        listView.setFooterDividersEnabled(false);
        listView.addFooterView(new View(GroupCallActivity.this));
        listView.setDividerHeight(2);

        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM | Gravity.LEFT;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.width = (int) Math.round(getWindowWidth() * 0.8);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(layoutParams);
        alertDialog.show();
    }


    private void showAudioInputDialog() {
        Set<AppRTCAudioManager.AudioDevice> mics = meeting.getMics();
        ListItem audioDeviceListItem = null;
        ArrayList<ListItem> audioDeviceList = new ArrayList<>();
        // Prepare list
        String item;
        for (int i = 0; i < mics.size(); i++) {
            item = mics.toArray()[i].toString();
            String mic = item.substring(0, 1).toUpperCase() + item.substring(1).toLowerCase();
            mic = mic.replace("_", " ");
            audioDeviceListItem = new ListItem(mic, null, item.equals(selectedAudioDeviceName));
            audioDeviceList.add(audioDeviceListItem);
        }

        ArrayAdapter arrayAdapter = new AudioDeviceListAdapter(GroupCallActivity.this, R.layout.audio_device_list_layout, audioDeviceList);

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(GroupCallActivity.this, R.style.AlertDialogCustom)
                .setAdapter(arrayAdapter, (dialog, which) -> {
                    AppRTCAudioManager.AudioDevice audioDevice = null;
                    switch (audioDeviceList.get(which).getItemName()) {
                        case "Bluetooth":
                            audioDevice = AppRTCAudioManager.AudioDevice.BLUETOOTH;
                            break;
                        case "Wired headset":
                            audioDevice = AppRTCAudioManager.AudioDevice.WIRED_HEADSET;
                            break;
                        case "Speaker phone":
                            audioDevice = AppRTCAudioManager.AudioDevice.SPEAKER_PHONE;
                            break;
                        case "Earpiece":
                            audioDevice = AppRTCAudioManager.AudioDevice.EARPIECE;
                            break;
                    }

                    JSONObject noiseConfig = new JSONObject();
                    JsonUtils.jsonPut(noiseConfig, "acousticEchoCancellation", true);
                    JsonUtils.jsonPut(noiseConfig, "noiseSuppression", true);
                    JsonUtils.jsonPut(noiseConfig, "autoGainControl", true);

                    meeting.changeMic(audioDevice, VideoSDK.createAudioTrack("high_quality", noiseConfig, this));
                });

        AlertDialog alertDialog = materialAlertDialogBuilder.create();

        ListView listView = alertDialog.getListView();
        listView.setDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.md_grey_200))); // set color
        listView.setFooterDividersEnabled(false);
        listView.addFooterView(new View(GroupCallActivity.this));
        listView.setDividerHeight(2);

        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM | Gravity.LEFT;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.width = (int) Math.round(getWindowWidth() * 0.6);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(layoutParams);

        alertDialog.show();
    }

    private void showMoreOptionsDialog() {
        int participantSize = meeting.getParticipants().size() + 1;
        ArrayList<ListItem> moreOptionsArrayList = new ArrayList<>();
        ListItem raised_hand = new ListItem("Raise Hand", AppCompatResources.getDrawable(GroupCallActivity.this, R.drawable.raise_hand));
        ListItem start_screen_share = new ListItem("Share screen", AppCompatResources.getDrawable(GroupCallActivity.this, R.drawable.ic_screen_share));
        ListItem stop_screen_share = new ListItem("Stop screen share", AppCompatResources.getDrawable(GroupCallActivity.this, R.drawable.ic_screen_share));
        ListItem start_recording = new ListItem("Start recording", AppCompatResources.getDrawable(GroupCallActivity.this, R.drawable.ic_recording));
        ListItem stop_recording = new ListItem("Stop recording", AppCompatResources.getDrawable(GroupCallActivity.this, R.drawable.ic_recording));
        ListItem participant_list = new ListItem("Participants (" + participantSize + ")", AppCompatResources.getDrawable(GroupCallActivity.this, R.drawable.ic_people));

        moreOptionsArrayList.add(raised_hand);

        if (localScreenShare) {
            moreOptionsArrayList.add(stop_screen_share);
        } else {
            moreOptionsArrayList.add(start_screen_share);
        }

        if (recording) {
            moreOptionsArrayList.add(stop_recording);
        } else {
            moreOptionsArrayList.add(start_recording);

        }

        moreOptionsArrayList.add(participant_list);


        ArrayAdapter arrayAdapter = new MoreOptionsListAdapter(GroupCallActivity.this, R.layout.more_options_list_layout, moreOptionsArrayList);
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(GroupCallActivity.this, R.style.AlertDialogCustom)
                .setAdapter(arrayAdapter, (dialog, which) -> {
                    switch (which) {
                        case 0: {
                            raisedHand();
                            break;
                        }
                        case 1: {
                            toggleScreenSharing();
                            break;
                        }
                        case 2: {
                            toggleRecording();
                            break;
                        }
                        case 3: {
                            openParticipantList();
                            break;
                        }
                    }
                });

        AlertDialog alertDialog = materialAlertDialogBuilder.create();

        ListView listView = alertDialog.getListView();
        listView.setDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.md_grey_200))); // set color
        listView.setFooterDividersEnabled(false);
        listView.addFooterView(new View(GroupCallActivity.this));
        listView.setDividerHeight(2);

        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM | Gravity.RIGHT;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.width = (int) Math.round(getWindowWidth() * 0.8);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(layoutParams);
        alertDialog.show();
    }

    private void raisedHand() {
        meeting.pubSub.publish("RAISE_HAND", "Raise Hand by Me", new PubSubPublishOptions());
    }

    private void toggleRecording() {
        if (!recording) {
            recordingStatusSnackbar.show();
            meeting.startRecording(null);

        } else {
            meeting.stopRecording();
        }
    }

    @Override
    public void onBackPressed() {
        showLeaveOrEndDialog();
    }

    @Override
    protected void onDestroy() {

        if (meeting != null) {
            meeting.removeAllListeners();
            meeting.getLocalParticipant().removeAllListeners();
            meeting.leave();
            meeting = null;
        }
        if (svrShare != null) {
            svrShare.clearImage();
            svrShare.setVisibility(View.GONE);
            shareLayout.setVisibility(View.GONE);
            svrShare.release();
        }

        super.onDestroy();
    }

    public void unSubscribeTopics() {
        if (meeting != null) {
            meeting.pubSub.unsubscribe("CHAT", chatListener);
            meeting.pubSub.unsubscribe("RAISE_HAND", raiseHandListener);
        }
    }

    public void openParticipantList() {
        RecyclerView participantsListView;
        ImageView close;
        bottomSheetDialog = new BottomSheetDialog(this);
        View v3 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_participants_list_view, findViewById(R.id.layout_participants));
        bottomSheetDialog.setContentView(v3);
        participantsListView = v3.findViewById(R.id.rvParticipantsLinearView);
        ((TextView) v3.findViewById(R.id.participant_heading)).setTypeface(Roboto_font.getTypeFace(GroupCallActivity.this));
        close = v3.findViewById(R.id.ic_close);
        participantsListView.setMinimumHeight(getWindowHeight());
        bottomSheetDialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        meeting.addEventListener(meetingEventListener);
        participants = getAllParticipants();
        participantsListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        participantsListView.setAdapter(new ParticipantListAdapter(participants, meeting, GroupCallActivity.this));
        participantsListView.setHasFixedSize(true);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        (GroupCallActivity.this).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private int getWindowWidth() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        (GroupCallActivity.this).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public ArrayList<Participant> getAllParticipants() {
        ArrayList participantList = new ArrayList();
        final Iterator<Participant> participants = meeting.getParticipants().values().iterator();
        for (int i = 0; i < meeting.getParticipants().size(); i++) {
            final Participant participant = participants.next();
            participantList.add(participant);

        }
        return participantList;
    }


    @SuppressLint("ClickableViewAccessibility")
    public void openChat() {
        RecyclerView messageRcv;
        ImageView close;
        bottomSheetDialog = new BottomSheetDialog(this);
        View v3 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_chat, findViewById(R.id.layout_chat));
        bottomSheetDialog.setContentView(v3);

        messageRcv = v3.findViewById(R.id.messageRcv);
        messageRcv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        RelativeLayout.LayoutParams lp =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getWindowHeight() / 2);
        messageRcv.setLayoutParams(lp);

        BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback
                = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet,
                                       @BottomSheetBehavior.State int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    RelativeLayout.LayoutParams lp =
                            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getWindowHeight() / 2);
                    messageRcv.setLayoutParams(lp);
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    RelativeLayout.LayoutParams lp =
                            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    messageRcv.setLayoutParams(lp);
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };

        bottomSheetDialog.getBehavior().addBottomSheetCallback(mBottomSheetCallback);

        etmessage = v3.findViewById(R.id.etMessage);
        etmessage.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.etMessage) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        ImageButton btnSend = v3.findViewById(R.id.btnSend);
        btnSend.setEnabled(false);
        etmessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etmessage.setHint("");
                }
            }
        });

        etmessage.setVerticalScrollBarEnabled(true);
        etmessage.setScrollbarFadingEnabled(false);

        etmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!etmessage.getText().toString().trim().isEmpty()) {
                    btnSend.setEnabled(true);
                    btnSend.setSelected(true);
                } else {
                    btnSend.setEnabled(false);
                    btnSend.setSelected(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //
        pubSubMessageListener = new PubSubMessageListener() {
            @Override
            public void onMessageReceived(PubSubMessage message) {
                messageAdapter.addItem(message);
                messageRcv.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        };

        // Subscribe for 'CHAT' topic
        List<PubSubMessage> pubSubMessageList = meeting.pubSub.subscribe("CHAT", pubSubMessageListener);

        //
        messageAdapter = new MessageAdapter(this, R.layout.item_message_list, pubSubMessageList, meeting);
        messageRcv.setAdapter(messageAdapter);
        messageRcv.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) ->
                messageRcv.scrollToPosition(messageAdapter.getItemCount() - 1));

        v3.findViewById(R.id.btnSend).setOnClickListener(view -> {
            String message = etmessage.getText().toString();
            if (!message.equals("")) {
                PubSubPublishOptions publishOptions = new PubSubPublishOptions();
                publishOptions.setPersist(true);

                meeting.pubSub.publish("CHAT", message, publishOptions);
                etmessage.setText("");
            } else {
                Toast.makeText(GroupCallActivity.this, "Please Enter Message",
                        Toast.LENGTH_SHORT).show();
            }

        });


        close = v3.findViewById(R.id.ic_close);
        bottomSheetDialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                meeting.pubSub.unsubscribe("CHAT", pubSubMessageListener);
            }
        });

    }

    public void showMeetingTime() {

        runnable = new Runnable() {
            @Override
            public void run() {
                int hours = meetingSeconds / 3600;
                int minutes = (meetingSeconds % 3600) / 60;
                int secs = meetingSeconds % 60;

                // Format the seconds into minutes,seconds.
                String time = String.format(Locale.getDefault(),
                        "%02d:%02d:%02d", hours,
                        minutes, secs);

                txtMeetingTime.setText(time);

                meetingSeconds++;

                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnable);

    }

    private void showMicRequestDialog(MicRequestListener listener) {
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(GroupCallActivity.this, R.style.AlertDialogCustom).create();
        alertDialog.setCanceledOnTouchOutside(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_layout, null);
        alertDialog.setView(dialogView);

        TextView title = (TextView) dialogView.findViewById(R.id.title);
        title.setVisibility(View.GONE);
        TextView message = (TextView) dialogView.findViewById(R.id.message);
        message.setText("Host is asking you to unmute your mic, do you want to allow ?");

        Button positiveButton = dialogView.findViewById(R.id.positiveBtn);
        positiveButton.setText("Yes");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.accept();
                alertDialog.dismiss();
            }
        });

        Button negativeButton = dialogView.findViewById(R.id.negativeBtn);
        negativeButton.setText("No");
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.reject();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }


    private void showWebcamRequestDialog(WebcamRequestListener listener) {
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(GroupCallActivity.this, R.style.AlertDialogCustom).create();
        alertDialog.setCanceledOnTouchOutside(false);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_layout, null);
        alertDialog.setView(dialogView);

        TextView title = (TextView) dialogView.findViewById(R.id.title);
        title.setVisibility(View.GONE);
        TextView message = (TextView) dialogView.findViewById(R.id.message);
        message.setText("Host is asking you to enable your webcam, do you want to allow ?");

        Button positiveButton = dialogView.findViewById(R.id.positiveBtn);
        positiveButton.setText("Yes");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.accept();
                alertDialog.dismiss();
            }
        });

        Button negativeButton = dialogView.findViewById(R.id.negativeBtn);
        negativeButton.setText("No");
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.reject();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }
    private void startRecordingScreen() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent permissionIntent = mediaProjectionManager != null ? mediaProjectionManager.createScreenCaptureIntent() : null;
        startActivityForResult(permissionIntent, SCREEN_RECORD_REQUEST_CODE);
    }

    @Override
    public void HBRecorderOnStart() {

    }

    @Override
    public void HBRecorderOnComplete() {

    }

    @Override
    public void HBRecorderOnError(int errorCode, String reason) {

    }
}