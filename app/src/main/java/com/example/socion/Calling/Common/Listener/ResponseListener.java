package com.example.socion.Calling.Common.Listener;

public interface ResponseListener {

    void onResponse(String meetingId);

    void onMeetingTimeChanged(int meetingTime);

}
