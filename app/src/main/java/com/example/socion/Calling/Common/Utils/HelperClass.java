package com.example.socion.Calling.Common.Utils;

import android.app.Dialog;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.socion.Calling.Common.Roboto_font;
import com.example.socion.R;


public class HelperClass {

    private static Dialog progressDialog;

    public static void setSnackBarStyle(View snackbarView, int textColor) {

        int snackbarTextId = com.google.android.material.R.id.snackbar_text;
        TextView textView = (TextView) snackbarView.findViewById(snackbarTextId);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
        params.height = 150;
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(params);

        if (textColor == 0) {
            textView.setTextColor(Color.BLACK);
        } else {
            textView.setTextColor(textColor);
        }
        textView.setTextSize(15);
        textView.setTypeface(Roboto_font.getTypeFace(snackbarView.getContext()));

    }

    private static void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    public static void showProgress(View view) {
        setViewAndChildrenEnabled(view, false);
        progressDialog = new Dialog(view.getContext(), R.style.ProgressDialogStyle);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.joinmeeting_progress_layout);
        WindowManager.LayoutParams wmlp = progressDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER | Gravity.CENTER;
        wmlp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        wmlp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        progressDialog.getWindow().setWindowAnimations(R.style.DialogNoAnimation);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgress(View view) {
        progressDialog.dismiss();
        progressDialog.cancel();
        setViewAndChildrenEnabled(view, true);
    }

    public static void checkParticipantSize(View view, View layout) {
        setViewAndChildrenEnabled(view, false);
        Dialog leaveprogressDialog = new Dialog(view.getContext(), R.style.ProgressDialogStyle);
        leaveprogressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        leaveprogressDialog.setContentView(layout);
        leaveprogressDialog.getWindow().setWindowAnimations(R.style.DialogNoAnimation);
        WindowManager.LayoutParams wmlp = leaveprogressDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER | Gravity.CENTER;
        wmlp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        wmlp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        leaveprogressDialog.setCanceledOnTouchOutside(false);
        leaveprogressDialog.setCancelable(false);
        leaveprogressDialog.show();
    }

    public static boolean isNullOrEmpty(String str) {
        return "null".equals(str) || "".equals(str) || null == str;
    }


}
