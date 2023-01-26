package com.example.socion.Calling.Common.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class CreateMeetingFragment extends Fragment {

    String name;
    String meeting;
    String meetingMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_meeting, container, false);


        Button btnJoin = view.findViewById(R.id.btnJoin);
        TextView tv = view.findViewById(R.id.meetingCreateMode);
        name = getArguments().getString("name");
        meeting = getArguments().getString("meeting");
        meetingMode = getArguments().getString("meetingMode");
        String mode = meetingMode+" "+meeting;
        tv.setText(mode.toUpperCase());

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getContext(), "Please Enter Name", Toast.LENGTH_SHORT).show();
                } else {
                    NetworkUtils networkUtils = new NetworkUtils(getContext());

                    if (networkUtils.isNetworkAvailable()) {
                        networkUtils.getToken(new ResponseListener() {
                            @Override
                            public void onResponse(String token) {
                                Log.i("Meeting",token.toString());
                                networkUtils.createMeeting(token, new ResponseListener() {
                                    @Override
                                    public void onResponse(String meetingId) {
                                        tv.setText(meetingId);
                                        Log.i("Meeting",meetingId);
                                        Intent intent = null;
                                        if (!TextUtils.isEmpty(meeting)) {
                                            if (meeting.equals("One to One Meeting")) {
                                                Log.e("Meeting",meeting);
                                                intent = new Intent((CreateOrJoinActivity) getActivity(), OneToOneCallActivity.class);
                                            } else {
                                                Log.e("Meeting",meeting + "group");
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
                        Snackbar snackbar = Snackbar.make(view.findViewById(R.id.createMeetingLayout), "No Internet Connection", Snackbar.LENGTH_LONG);
                        HelperClass.setSnackBarStyle(snackbar.getView(), 0);
                        snackbar.show();
                    }
                }
            }
        });
        return view;
    }
}