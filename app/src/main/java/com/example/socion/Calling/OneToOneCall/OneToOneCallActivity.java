package com.example.socion.Calling.OneToOneCall;

import android.Manifest;
import android.annotation.SuppressLint;
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
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.example.socion.Calling.GroupCall.Utils.ParticipantState;
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
import java.util.Timer;

import javax.xml.transform.Result;

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
import pl.droidsonroids.gif.GifImageView;

public class OneToOneCallActivity extends AppCompatActivity implements HBRecorderListener {
    //--------------------------------------------------------------------------------------
    HBRecorder hbRecorder; //video Recorder

    private static final int SCREEN_RECORD_REQUEST_CODE = 100;
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 101;
    private static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = 102;
//--------------------------------------------------------------------------------------
    private static Meeting meeting;
    private SurfaceViewRenderer svrLocal;
    private SurfaceViewRenderer svrParticipant;
    private FloatingActionButton btnWebcam;
    private ImageButton btnMic, btnAudioSelection, btnSwitchCameraMode;
    private FloatingActionButton btnLeave, btnChat, btnMore;
    private CardView localCard, participantCard;
    private LinearLayout micLayout;
    ArrayList<Participant> participants;
    private GifImageView img_localActiveSpeaker, img_participantActiveSpeaker;
    private TextView txtLocalParticipantName, txtParticipantName, tvName, tvLocalParticipantName;
    private String participantName;

    private VideoTrack participantTrack = null;

    private boolean micEnabled = true;
    private boolean webcamEnabled = true;
    private boolean recording = false;
    private boolean localScreenShare = false;
    private boolean fullScreen = false;
    private static String token;
    int clickCount = 0;
    long startTime;
    static final int MAX_DURATION = 500;
    private Snackbar recordingStatusSnackbar;


    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;

    private Timer timer = new Timer();
    private boolean screenshareEnabled = false;
    private VideoTrack localTrack = null;
    private VideoTrack screenshareTrack;
    private BottomSheetDialog bottomSheetDialog;
    private String selectedAudioDeviceName;

    private EditText etmessage;
    private MessageAdapter messageAdapter;
    private PubSubMessageListener pubSubMessageListener;
    private int meetingSeconds;
    private TextView txtMeetingTime;
    private Snackbar screenShareParticipantNameSnackbar;

    private Runnable runnable;
    final Handler handler = new Handler();

    private PubSubMessageListener chatListener;
//----------------------------------------------
    private MediaRecorder recorder; //audio
    private String mFilename = null;
    private String mFilenameVideo = null;

    ProgressDialog progressDialog;
    StorageReference storageReference;
    public static final String TAG = "OneToOne";
    public static boolean audioProcessed;
    public static boolean videoProcessed;
    boolean isVideoOn = false;

    //--------------------------------

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_call);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hbRecorder = new HBRecorder(this, this);
        hbRecorder.isAudioEnabled(false);
        hbRecorder.setMaxDuration(60);
//        hbRecorder.setVideoFrameRate(1);
        hbRecorder.setScreenDimensions(640, 320);
        hbRecorder.setVideoEncoder("H264");

        //-------------
        String music = "recording";
        mFilename = getApplication().getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + music + ".m4a";
        //------------------
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
        localCard = findViewById(R.id.LocalCard);
        participantCard = findViewById(R.id.ParticipantCard);

        txtLocalParticipantName = findViewById(R.id.txtLocalParticipantName);
        txtParticipantName = findViewById(R.id.txtParticipantName);
        tvName = findViewById(R.id.tvName);
        tvLocalParticipantName = findViewById(R.id.tvLocalParticipantName);

        svrLocal = findViewById(R.id.svrLocal);
        svrLocal.init(PeerConnectionUtils.getEglContext(), null);
        svrLocal.setMirror(true);

        svrParticipant = findViewById(R.id.svrParticipant);
        svrParticipant.init(PeerConnectionUtils.getEglContext(), null);

        btnMic = findViewById(R.id.btnMic);
        btnWebcam = findViewById(R.id.btnWebcam);
        btnAudioSelection = findViewById(R.id.btnAudioSelection);
        txtMeetingTime = findViewById(R.id.txtMeetingTime);

        img_localActiveSpeaker = findViewById(R.id.img_localActiveSpeaker);
        img_participantActiveSpeaker = findViewById(R.id.img_participantActiveSpeaker);

        token = getIntent().getStringExtra("token");

        Log.i(TAG, token);
        final String meetingId = getIntent().getStringExtra("meetingId");
        micEnabled = getIntent().getBooleanExtra("micEnabled", true);
        webcamEnabled = getIntent().getBooleanExtra("webcamEnabled", true);

        String localParticipantName = getIntent().getStringExtra("participantName");
        if (localParticipantName == null) {
            localParticipantName = "Naqvi";
        }
        txtLocalParticipantName.setText(localParticipantName.substring(0, 1));
        tvLocalParticipantName.setText("You");
        //

        TextView textMeetingId = findViewById(R.id.txtMeetingId);
        textMeetingId.setText(meetingId);

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
                OneToOneCallActivity.this, meetingId, localParticipantName,
                false, false, null, customTracks
        );

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

        recordingStatusSnackbar = Snackbar.make(findViewById(R.id.mainLayout), "Recording will be started in few moments",
                Snackbar.LENGTH_INDEFINITE);
        HelperClass.setSnackBarStyle(recordingStatusSnackbar.getView(), 0);
        recordingStatusSnackbar.setGestureInsetBottomIgnored(true);

        ((FrameLayout) findViewById(R.id.participants_frameLayout)).setOnTouchListener(new View.OnTouchListener() {
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
                                    params.setMargins(30, 10, 0, 0);
                                    findViewById(R.id.meetingLayout).setLayoutParams(params);

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
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void toggleMicIcon(boolean micEnabled) {
        if (micEnabled) {
            btnMic.setImageResource(R.drawable.ic_mic_on);
            btnAudioSelection.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
            micLayout.setBackground(ContextCompat.getDrawable(OneToOneCallActivity.this, R.drawable.layout_selected));
        } else {
            btnMic.setImageResource(R.drawable.ic_mic_off_24);
            btnAudioSelection.setImageResource(R.drawable.ic_baseline_arrow_drop_down);
            micLayout.setBackgroundColor(Color.WHITE);
            micLayout.setBackground(ContextCompat.getDrawable(OneToOneCallActivity.this, R.drawable.layout_nonselected));
        }
    }

    @SuppressLint("ResourceType")
    private void toggleWebcamIcon(Boolean webcamEnabled) {
        if (webcamEnabled) {
            btnWebcam.setImageResource(R.drawable.ic_video_camera);
            btnWebcam.setColorFilter(Color.WHITE);
            Drawable buttonDrawable = btnWebcam.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            if (buttonDrawable != null) DrawableCompat.setTint(buttonDrawable, Color.TRANSPARENT);
            btnWebcam.setBackground(buttonDrawable);
            isVideoOn = true; //If video is on

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
//---------------------------------------------------------------------
    private final MeetingEventListener meetingEventListener = new MeetingEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onMeetingJoined() {

            if (meeting != null) {
                startRecording();
                //hide progress when meetingJoined
                HelperClass.hideProgress(getWindow().getDecorView().getRootView());

                localCard.setVisibility(View.VISIBLE);

                // if more than 2 participant join than leave the meeting (For Joining)
                if (meeting.getParticipants().size() <= 1) {
                    toggleMicIcon(micEnabled);



                    micEnabled = !micEnabled;
                    webcamEnabled = !webcamEnabled;

                    toggleMic();
                    toggleWebCam();

                    // Local participant listeners
                    setLocalListeners();
                    new NetworkUtils(OneToOneCallActivity.this).fetchMeetingTime(meeting.getMeetingId(), token, new ResponseListener() {
                        @Override
                        public void onResponse(String meetingTime) {
                        }

                        @Override
                        public void onMeetingTimeChanged(int meetingTime) {
                            meetingSeconds = meetingTime;
                            showMeetingTime();
                        }
                    });

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
                                AlertDialog alertDialog = new MaterialAlertDialogBuilder(OneToOneCallActivity.this, R.style.AlertDialogCustom).create();
                                alertDialog.setCanceledOnTouchOutside(false);

                                LayoutInflater inflater = OneToOneCallActivity.this.getLayoutInflater();
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

                } else {
                    View progressLayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.progress_layout, findViewById(R.id.layout_progress));


                    if (!((Activity) OneToOneCallActivity.this).isFinishing())
                        HelperClass.checkParticipantSize(getWindow().getDecorView().getRootView(), progressLayout);

                    progressLayout.findViewById(R.id.leaveBtn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            meeting.leave();
                        }
                    });

                }
            }
        }
    //---------------------------------------------------------------------
        private void startRecording() {
            Toast.makeText(OneToOneCallActivity.this, "Recording Started", Toast.LENGTH_LONG).show();
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


        String DownloadAudioUrl = "";
        String DownloadVideoUrl = "";

        private void stopRecording() {

            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
                uploadAudio();
                Log.i("OneToOne: (stopRecording)", DownloadAudioUrl);
            }

        }

    //---------------------------------------------------------------------
        private void uploadAudio() {
            final Result[] result = new Result[1];
            progressDialog.setMessage("Uploading Audio");
            progressDialog.setCancelable(false);
            progressDialog.show();
            //Unique Name of File
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
            String saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            String saveCurrentTime = currentTime.format(calendar.getTime());

            String audioName = saveCurrentDate + saveCurrentTime + ".m4a";

            StorageReference filepath = storageReference.child("Audio").child(audioName);

            Uri uri = Uri.fromFile(new File(mFilename));//--------------------------------------------------------------------- Pick File
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
                                Log.i("OneToOne", "Audio Link " + DownloadAudioUrl);
                                Toast.makeText(getApplicationContext(), "Audio Processed Successfully!", Toast.LENGTH_SHORT).show();
                                audioProcessed = true;
                                if (hbRecorder.isBusyRecording()) {
                                    hbRecorder.stopScreenRecording();
                                    uploadVideo(DownloadAudioUrl);
                                } else {
                                    //Only Pass AudioURL Link
                                    Intent intents = new Intent(OneToOneCallActivity.this, DashboardActivity.class);
                                    intents.putExtra("audioURL", DownloadAudioUrl);
                                    intents.putExtra("videoURL", "");
                                    intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intents);
                                    finish();
                                }

                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            });
        }

        private void uploadVideo(String downloadAudioUrl) {
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
                                Log.e("OneToOne", "Video Link " + DownloadVideoUrl);
                                videoProcessed = true;
                                progressDialog.dismiss();

                                //Only Pass AudioURL Link
                                Intent intents = new Intent(OneToOneCallActivity.this, DashboardActivity.class);
                                intents.putExtra("audioURL", downloadAudioUrl);
                                intents.putExtra("videoURL", DownloadVideoUrl);
                                intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intents);
                                finish();

                            }
                        }
                    });
                }
            });


        }

        @Override
        public void onMeetingLeft() {
            if (!isDestroyed()) {
                stopRecording();
            }
        }

        @Override
        public void onParticipantJoined(Participant participant) {
            //We will start recording here
            if (meeting.getParticipants().size() < 2) {
                showParticipantCard();
                txtParticipantName.setText(participant.getDisplayName().substring(0, 1));
                participantName = participant.getDisplayName();
                tvName.setText(participantName);
                Toast.makeText(OneToOneCallActivity.this, participant.getDisplayName() + " joined",
                        Toast.LENGTH_SHORT).show();
            }
            participant.addEventListener(participantEventListener);
        }

        @Override
        public void onParticipantLeft(Participant participant) {
            if (meeting.getParticipants().size() < 1) {
                hideParticipantCard();
                if (screenshareTrack != null) {
                    if (participantTrack != null) participantTrack.removeSink(svrLocal);
                    svrLocal.clearImage();
                    svrLocal.setVisibility(View.GONE);
                    showParticipantCard();
                    if (localTrack != null) {
                        localTrack.addSink(svrLocal);
                        svrLocal.setVisibility(View.VISIBLE);
                    }
                    screenshareTrack.addSink(svrParticipant);
                    svrParticipant.setVisibility(View.VISIBLE);

                }
                Toast.makeText(OneToOneCallActivity.this, participant.getDisplayName() + " left",
                        Toast.LENGTH_SHORT).show();
            }

            participant.removeAllListeners();
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
            Toast.makeText(OneToOneCallActivity.this, "Recording started",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRecordingStopped() {
            recording = false;

            (findViewById(R.id.recordingLottie)).setVisibility(View.GONE);

            Toast.makeText(OneToOneCallActivity.this, "Recording stopped",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onExternalCallStarted() {
            Toast.makeText(OneToOneCallActivity.this, "onExternalCallStarted", Toast.LENGTH_SHORT).show();
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
            if (!HelperClass.isNullOrEmpty(participantId)) {
                if (participantId.equals(meeting.getLocalParticipant().getId())) {
                    img_localActiveSpeaker.setVisibility(View.VISIBLE);
                    img_participantActiveSpeaker.setVisibility(View.GONE);
                } else {
                    img_participantActiveSpeaker.setVisibility(View.VISIBLE);
                    img_localActiveSpeaker.setVisibility(View.GONE);
                }
            } else {
                img_participantActiveSpeaker.setVisibility(View.GONE);
                img_localActiveSpeaker.setVisibility(View.GONE);
            }
        }

        @Override
        public void onMeetingStateChanged(String state) {
            if (state == "FAILED") {
                View parentLayout = findViewById(android.R.id.content);
                SpannableStringBuilder builderTextLeft = new SpannableStringBuilder();
                builderTextLeft.append("   Call disconnected. Reconnecting...");
                builderTextLeft.setSpan(new ImageSpan(OneToOneCallActivity.this, R.drawable.ic_call_disconnected), 0, 1, 0);
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

    private void showParticipantCard() {
        localCard.setLayoutParams(new CardView.LayoutParams(getWindowWidth() / 4, getWindowHeight() / 5, Gravity.RIGHT | Gravity.BOTTOM));
        ViewGroup.MarginLayoutParams cardViewMarginParams = (ViewGroup.MarginLayoutParams) localCard.getLayoutParams();
        cardViewMarginParams.setMargins(30, 0, 60, 40);
        localCard.requestLayout();
        txtLocalParticipantName.setLayoutParams(new FrameLayout.LayoutParams(120, 120, Gravity.CENTER));
        txtLocalParticipantName.setTextSize(24);
        txtLocalParticipantName.setGravity(Gravity.CENTER);
        tvLocalParticipantName.setVisibility(View.GONE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(50, 50, Gravity.RIGHT);
        layoutParams.setMargins(0, 12, 12, 0);
        img_localActiveSpeaker.setLayoutParams(layoutParams);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtLocalParticipantName.setForegroundGravity(Gravity.CENTER);
        }
        participantCard.setVisibility(View.VISIBLE);

    }

    private void hideParticipantCard() {
        localCard.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ViewGroup.MarginLayoutParams cardViewMarginParams = (ViewGroup.MarginLayoutParams) localCard.getLayoutParams();
        cardViewMarginParams.setMargins(30, 5, 30, 30);
        localCard.requestLayout();
        txtLocalParticipantName.setLayoutParams(new FrameLayout.LayoutParams(220, 220, Gravity.CENTER));
        txtLocalParticipantName.setTextSize(40);
        txtLocalParticipantName.setGravity(Gravity.CENTER);
        tvLocalParticipantName.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(75, 75, Gravity.RIGHT);
        layoutParams.setMargins(0, 30, 30, 0);
        img_localActiveSpeaker.setLayoutParams(layoutParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txtLocalParticipantName.setForegroundGravity(Gravity.CENTER);
        }
        participantCard.setVisibility(View.GONE);
    }


    private void askPermissionForScreenShare() {
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getApplication().getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCREEN_RECORD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Start screen recording
                hbRecorder.startScreenRecording(data, resultCode);
            }
        }

        //Commented block is just used for screen capture but we don't need it that's why commented it out

//        if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE)
//            return;
//        if (resultCode != Activity.RESULT_OK) {
//            Toast.makeText(OneToOneCallActivity.this, "You didn't give permission to capture the screen.", Toast.LENGTH_SHORT).show();
//            localScreenShare = false;
//            return;
//        }
        //meeting.enableScreenShare(data);
    }

    private void updatePresenter(String participantId) {
        if (participantId == null) {
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

        screenshareTrack = (VideoTrack) shareStream.getTrack();
        if (participantName != null)
            txtLocalParticipantName.setText(participantName.substring(0, 1));

        tvName.setText(participantName + " is presenting");
        onTrackChange();

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
                    screenshareTrack = null;
                    track.removeSink(svrParticipant);
                    svrParticipant.clearImage();
                    svrParticipant.setVisibility(View.GONE);
                    removeTrack(participantTrack, true);
                    txtLocalParticipantName.setText(meeting.getLocalParticipant().getDisplayName().substring(0, 1));
                    tvName.setText(participantName);
                    onTrackChange();
                    screenshareEnabled = false;
                    localScreenShare = false;
                }
            }
        });
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

        Toast.makeText(OneToOneCallActivity.this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
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
        micEnabled = !micEnabled;
    }

    private void toggleWebCam() {
        if (webcamEnabled) {
            Toast.makeText(this, "Camera off ", Toast.LENGTH_SHORT).show();
            meeting.disableWebcam();
            //adding function to pause Media
            if (hbRecorder.isBusyRecording()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    hbRecorder.pauseScreenRecording();
                }
            }
        } else {
            Toast.makeText(this, "Camera On", Toast.LENGTH_SHORT).show();

            CustomStreamTrack videoCustomTrack = VideoSDK.createCameraVideoTrack("h720p_w960p", "front", CustomStreamTrack.VideoMode.DETAIL, this);
            meeting.enableWebcam(videoCustomTrack);
            if (!hbRecorder.isBusyRecording()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    hbRecorder.resumeScreenRecording();
                }
            }
        }

        webcamEnabled = !webcamEnabled;
        toggleWebcamIcon(webcamEnabled);
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


    private void showLeaveOrEndDialog() {
        ArrayList<ListItem> OptionsArrayList = new ArrayList<>();
        ListItem leaveMeeting = new ListItem("Leave", "Only you will leave the call", AppCompatResources.getDrawable(OneToOneCallActivity.this, R.drawable.ic_leave));
        ListItem endMeeting = new ListItem("End", "End call for all the participants", AppCompatResources.getDrawable(OneToOneCallActivity.this, R.drawable.ic_end_meeting));

        OptionsArrayList.add(leaveMeeting);
        OptionsArrayList.add(endMeeting);

        ArrayAdapter arrayAdapter = new LeaveOptionListAdapter(OneToOneCallActivity.this, R.layout.leave_options_list_layout, OptionsArrayList);
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(OneToOneCallActivity.this, R.style.AlertDialogCustom)
                .setAdapter(arrayAdapter, (dialog, which) -> {
                    switch (which) {
                        case 0: {
                            unSubscribeTopics();
                            meeting.leave();
                            break;
                        }
                        case 1: {
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
        listView.addFooterView(new View(OneToOneCallActivity.this));
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

        ArrayAdapter arrayAdapter = new AudioDeviceListAdapter(OneToOneCallActivity.this, R.layout.audio_device_list_layout, audioDeviceList);

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(OneToOneCallActivity.this, R.style.AlertDialogCustom)
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
        listView.addFooterView(new View(OneToOneCallActivity.this));
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
        ListItem start_screen_share = new ListItem("Share screen", AppCompatResources.getDrawable(OneToOneCallActivity.this, R.drawable.ic_screen_share));
        ListItem stop_screen_share = new ListItem("Stop screen share", AppCompatResources.getDrawable(OneToOneCallActivity.this, R.drawable.ic_screen_share));
        ListItem start_recording = new ListItem("Start recording", AppCompatResources.getDrawable(OneToOneCallActivity.this, R.drawable.ic_recording));
        ListItem stop_recording = new ListItem("Stop recording", AppCompatResources.getDrawable(OneToOneCallActivity.this, R.drawable.ic_recording));
        ListItem participant_list = new ListItem("Participants (" + participantSize + ")", AppCompatResources.getDrawable(OneToOneCallActivity.this, R.drawable.ic_people));
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


        ArrayAdapter arrayAdapter = new MoreOptionsListAdapter(OneToOneCallActivity.this, R.layout.more_options_list_layout, moreOptionsArrayList);
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(OneToOneCallActivity.this, R.style.AlertDialogCustom)
                .setAdapter(arrayAdapter, (dialog, which) -> {
                    switch (which) {
                        case 0: {
                            toggleScreenSharing();
                            break;
                        }
                        case 1: {
                            toggleRecording();
                            break;
                        }
                        case 2: {
                            openParticipantList();
                            break;
                        }
                    }
                });

        AlertDialog alertDialog = materialAlertDialogBuilder.create();

        ListView listView = alertDialog.getListView();
        listView.setDivider(new ColorDrawable(ContextCompat.getColor(this, R.color.md_grey_200))); // set color
        listView.setFooterDividersEnabled(false);
        listView.addFooterView(new View(OneToOneCallActivity.this));
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

        if (svrParticipant != null) {
            svrParticipant.clearImage();
            svrParticipant.setVisibility(View.GONE);
            svrParticipant.release();
        }

        if (svrLocal != null) {
            svrLocal.clearImage();
            svrLocal.setVisibility(View.GONE);
            svrLocal.release();
        }

        timer.cancel();

        super.onDestroy();
    }

    private void unSubscribeTopics() {
        if (meeting != null) {
            meeting.pubSub.unsubscribe("CHAT", chatListener);
        }

    }

    private void onTrackChange() {
        if (screenshareTrack != null) {

            if (meeting.getParticipants().size() == 0) {
                showParticipantCard();
                if (localTrack != null) {
                    localTrack.addSink(svrLocal);
                    svrLocal.setVisibility(View.VISIBLE);
                }

            } else {
                if (localTrack != null) {
                    localTrack.removeSink(svrLocal);
                    svrLocal.clearImage();
                    svrLocal.setVisibility(View.GONE);
                }

                if (participantTrack != null) {
                    participantTrack.removeSink(svrParticipant);
                    svrParticipant.clearImage();
                    participantTrack.addSink(svrLocal);
                    if (participantName != null)
                        txtLocalParticipantName.setText(participantName.substring(0, 1));
                    svrLocal.setVisibility(View.VISIBLE);
                }
            }

            screenshareTrack.addSink(svrParticipant);
            svrParticipant.setVisibility(View.VISIBLE);
        } else {

            if (participantTrack != null) {
                svrParticipant.setVisibility(View.VISIBLE);
                participantTrack.addSink(svrParticipant);
                ((View) img_participantActiveSpeaker).bringToFront();
            }
            if (localTrack != null) {
                svrLocal.setVisibility(View.VISIBLE);
                svrLocal.setZOrderMediaOverlay(true);
                localTrack.addSink(svrLocal);
                ((View) img_localActiveSpeaker).bringToFront();
                ((View) localCard).bringToFront();

            }

        }


    }

    private void removeTrack(VideoTrack track, Boolean isLocal) {
        if (screenshareTrack == null) {
            if (isLocal) {
                if (track != null) track.removeSink(svrLocal);
                svrLocal.clearImage();
                svrLocal.setVisibility(View.GONE);
            } else {
                if (track != null) track.removeSink(svrParticipant);
                svrParticipant.clearImage();
                svrParticipant.setVisibility(View.GONE);
            }
        } else {
            if (!isLocal) {
                if (track != null) track.removeSink(svrLocal);
                svrLocal.clearImage();
                svrLocal.setVisibility(View.GONE);
                onTrackChange();
            } else {
                if (meeting.getParticipants().size() == 0) {
                    if (track != null) track.removeSink(svrLocal);
                    svrLocal.clearImage();
                    svrLocal.setVisibility(View.GONE);
                } else {
                    if (track != null) track.removeSink(svrParticipant);
                    svrParticipant.clearImage();
                    svrParticipant.setVisibility(View.GONE);
                    onTrackChange();
                }
            }
        }
    }

    private final ParticipantEventListener participantEventListener = new ParticipantEventListener() {
        @Override
        public void onStreamEnabled(Stream stream) {
            if (stream.getKind().equalsIgnoreCase("video")) {
                if (meeting.getParticipants().size() < 2) {
                    VideoTrack track = (VideoTrack) stream.getTrack();
                    participantTrack = track;
                    onTrackChange();
                    setQuality("high");
                }
            }
            if (stream.getKind().equalsIgnoreCase("audio")) {
                if (meeting.getParticipants().size() >= 2) {
                    stream.pause();
                }
            }
        }

        @Override
        public void onStreamDisabled(Stream stream) {
            if (stream.getKind().equalsIgnoreCase("video")) {
                if (meeting.getParticipants().size() < 2) {
                    VideoTrack track = (VideoTrack) stream.getTrack();
                    if (track != null) participantTrack = null;
                    removeTrack(track, false);
                }
            }
            if (stream.getKind().equalsIgnoreCase("audio")) {
                if (meeting.getParticipants().size() >= 2) {
                    stream.pause();
                }
            }
        }
    };

    private void setQuality(String quality) {
        final Iterator<Participant> participants = meeting.getParticipants().values().iterator();

        for (int i = 0; i < meeting.getParticipants().size(); i++) {
            Participant participant = participants.next();
            participant.setQuality(quality);
        }
    }

    private void setLocalListeners() {
        meeting.getLocalParticipant().addEventListener(new ParticipantEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onStreamEnabled(Stream stream) {
                if (stream.getKind().equalsIgnoreCase("video")) {
                    VideoTrack track = (VideoTrack) stream.getTrack();
                    localTrack = track;
                    onTrackChange();
                } else if (stream.getKind().equalsIgnoreCase("audio")) {
                    toggleMicIcon(true);
                } else if (stream.getKind().equalsIgnoreCase("share")) {
                    // display share video
                    VideoTrack videoTrack = (VideoTrack) stream.getTrack();
                    screenshareTrack = videoTrack;
                    if (participantName != null)
                        txtLocalParticipantName.setText(participantName.substring(0, 1));

                    tvName.setVisibility(View.GONE);

                    screenShareParticipantNameSnackbar = Snackbar.make(findViewById(R.id.mainLayout), "You started presenting",
                            Snackbar.LENGTH_SHORT);
                    HelperClass.setSnackBarStyle(screenShareParticipantNameSnackbar.getView(), 0);
                    screenShareParticipantNameSnackbar.setGestureInsetBottomIgnored(true);
                    screenShareParticipantNameSnackbar.getView().setOnClickListener(view -> screenShareParticipantNameSnackbar.dismiss());
                    screenShareParticipantNameSnackbar.show();

                    onTrackChange();
                    //
                    localScreenShare = true;
                    screenshareEnabled = true;
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onStreamDisabled(Stream stream) {
                if (stream.getKind().equalsIgnoreCase("video")) {
                    VideoTrack track = (VideoTrack) stream.getTrack();
                    if (track != null) localTrack = null;
                    removeTrack(track, true);
                    toggleWebcamIcon(false);
                } else if (stream.getKind().equalsIgnoreCase("audio")) {
                    toggleMicIcon(false);
                } else if (stream.getKind().equalsIgnoreCase("share")) {
                    VideoTrack track = (VideoTrack) stream.getTrack();
                    if (track != null) screenshareTrack = null;
                    track.removeSink(svrParticipant);
                    svrParticipant.clearImage();
                    svrParticipant.setVisibility(View.GONE);
                    if (meeting.getParticipants().size() == 0) hideParticipantCard();

                    removeTrack(participantTrack, true);
                    txtLocalParticipantName.setText(meeting.getLocalParticipant().getDisplayName().substring(0, 1));
                    tvName.setVisibility(View.VISIBLE);
                    onTrackChange();
                    //
                    localScreenShare = false;
                    screenshareEnabled = false;
                }
            }
        });
    }

    public void openParticipantList() {
        RecyclerView participantsListView;
        ImageView close;
        bottomSheetDialog = new BottomSheetDialog(this);
        View v3 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_participants_list_view, findViewById(R.id.layout_participants));
        bottomSheetDialog.setContentView(v3);
        participantsListView = v3.findViewById(R.id.rvParticipantsLinearView);
        ((TextView) v3.findViewById(R.id.participant_heading)).setTypeface(Roboto_font.getTypeFace(OneToOneCallActivity.this));
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
        participantsListView.setAdapter(new ParticipantListAdapter(participants, meeting, OneToOneCallActivity.this));
        participantsListView.setHasFixedSize(true);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        (OneToOneCallActivity.this).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private int getWindowWidth() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        (OneToOneCallActivity.this).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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
                Toast.makeText(OneToOneCallActivity.this, "Please Enter Message",
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
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(OneToOneCallActivity.this, R.style.AlertDialogCustom).create();
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
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(OneToOneCallActivity.this, R.style.AlertDialogCustom).create();
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
        if (webcamEnabled == true) {
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            Intent permissionIntent = mediaProjectionManager != null ? mediaProjectionManager.createScreenCaptureIntent() : null;
            startActivityForResult(permissionIntent, SCREEN_RECORD_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No Video Recording Started", Toast.LENGTH_SHORT).show();
        }

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