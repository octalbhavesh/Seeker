package com.octalsoftaware.sage.view.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.TextView;

import com.sage.android.R;
import com.valdesekamdem.library.mdtoast.MDToast;

public  class BaseFragment extends android.app.Fragment {


    public Context mContext;
    private Dialog dialog;

   /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return onCreateViewPost(inflater, container, savedInstanceState);
    }*/

    public void clickAnimation(TextView textView) {
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 0.3f);
        animation1.setDuration(200);
        animation1.setStartOffset(1000);
        textView.startAnimation(animation1);
    }



    public boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
        }
        return false;
    }

    public void sucessToast(Context context, String message) {
        MDToast mdToast = MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
        mdToast.show();
    }

    public void errorToast(Context context, String message) {
        MDToast mdToast = MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);
        mdToast.show();
    }

    public void warningToast(Context context, String message) {
        MDToast mdToast = MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING);
        mdToast.show();
    }

    public boolean checkValidation(EditText[] etTxt) {
        boolean status = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        for (int i = 0; i < etTxt.length; i++) {
            EditText et = etTxt[i];
            // String ss = String.valueOf(et.getHint().toString());
            if (et.getText().toString().trim().equalsIgnoreCase("")) {
                et.setError("Enter " + et.getHint() + "");
                et.setFocusable(true);
                status = false;
            } else {
                // email vaidation
                if (et.getHint().toString().trim().equalsIgnoreCase("email") ||
                        et.getHint().toString().trim().equalsIgnoreCase("email address")) {
                    if (!et.getText().toString().trim().matches(emailPattern)) {
                        et.setError("Enter " + "valid email" + "");
                        et.setFocusable(true);
                        status = false;
                    }
                }
                // phone number validation
                else if (et.getHint().toString().trim().equalsIgnoreCase("mobile number")
                        || et.getHint().toString().trim().equalsIgnoreCase("phone number")
                        || et.getHint().toString().trim().equalsIgnoreCase("phone")
                        || et.getHint().toString().trim().equalsIgnoreCase("mobile")) {
                    if (et.getText().toString().trim().length() < 6) {
                        et.setError("Enter " + "valid phone number" + "");
                        et.setFocusable(true);
                        status = false;
                    } else {
                        status = true;
                    }
                } else {
                    status = true;
                }
            }
        }
        return status;
    }

    public void showProgress(Context mContext) {
        dialog = new Dialog(mContext, android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custome_progress_dialog);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void hideProgress() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void customeFont(Context context, TextView tv) {
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "FaktPro-Normal.ttf");
        tv.setTypeface(custom_font);
    }
}