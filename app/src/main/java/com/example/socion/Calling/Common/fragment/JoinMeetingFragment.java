package com.example.socion.Calling.Common.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.example.socion.Calling.Common.Activity.CreateOrJoinActivity;
import com.example.socion.Calling.Common.Listener.ResponseListener;
import com.example.socion.Calling.Common.Utils.HelperClass;
import com.example.socion.Calling.Common.Utils.NetworkUtils;
import com.example.socion.Calling.GroupCall.Activity.GroupCallActivity;
import com.example.socion.Calling.OneToOneCall.OneToOneCallActivity;
import com.example.socion.R;
import com.google.android.material.snackbar.Snackbar;


public class JoinMeetingFragment extends Fragment {
    String name;
    String meeting;
    String meetingMode;

    public JoinMeetingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_join_meeting, container, false);

        EditText etMeetingId = view.findViewById(R.id.etMeetingId); //meeting Code

        Button btnJoin = view.findViewById(R.id.btnJoin);

        name = getArguments().getString("name");
        meeting = getArguments().getString("meeting");
        meetingMode = getArguments().getString("meetingMode");


        btnJoin.setOnClickListener(v -> {
            if ("".equals(etMeetingId.getText().toString().trim())) {
                Toast.makeText(getContext(), "Please enter meeting ID",
                        Toast.LENGTH_SHORT).show();
            } else if (!etMeetingId.getText().toString().trim().matches("\\w{4}\\-\\w{4}\\-\\w{4}")) {
                Toast.makeText(getContext(), "Please enter valid meeting ID",
                        Toast.LENGTH_SHORT).show();
            } else {
                NetworkUtils networkUtils = new NetworkUtils(getContext());
                if (networkUtils.isNetworkAvailable()) {
                    networkUtils.getToken(new ResponseListener() {
                        @Override
                        public void onResponse(String token) {
                            networkUtils.joinMeeting(token, etMeetingId.getText().toString().trim(), new ResponseListener() {
                                @Override
                                public void onResponse(String meetingId) {
                                    Intent intent = null;
                                    if (!TextUtils.isEmpty(meeting)) {
                                        if (meeting.equals("One to One Meeting")) {
                                            intent = new Intent((CreateOrJoinActivity) getActivity(), OneToOneCallActivity.class);
                                        } else {
                                            intent = new Intent((CreateOrJoinActivity) getActivity(), GroupCallActivity.class);
                                        }
                                        intent.putExtra("token", token);
                                        intent.putExtra("meetingId", meetingId);
                                        intent.putExtra("webcamEnabled", ((CreateOrJoinActivity) getActivity()).isWebcamEnabled());
                                        intent.putExtra("micEnabled", ((CreateOrJoinActivity) getActivity()).isMicEnabled());
                                        intent.putExtra("participantName", name);
                                        startActivity(intent);
                                        ((CreateOrJoinActivity) getActivity()).finish();
                                    } else {
                                        Toast.makeText(getContext(),
                                                "Please Choose Meeting Type", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onMeetingTimeChanged(int meetingTime) {

                                }
                            });
                        }

                        @Override
                        public void onMeetingTimeChanged(int meetingTime) {

                        }
                    });

                } else {
                    Snackbar snackbar =
                            Snackbar.make(view.findViewById(R.id.joinMeetingLayout), "No Internet Connection", Snackbar.LENGTH_LONG);
                    HelperClass.setSnackBarStyle(snackbar.getView(), 0);
                    snackbar.show();
                }
            }

        });
        return view;
    }
}