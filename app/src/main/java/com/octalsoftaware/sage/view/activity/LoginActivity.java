package com.octalsoftaware.sage.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.octalsoftaware.sage.SageSeekerApplication;
import com.octalsoftaware.sage.constants.S;
import com.octalsoftaware.sage.network.APIRequest;
import com.octalsoftaware.sage.network.MyApiEndpointInterface;
import com.octalsoftaware.sage.response.ServerLogin;
import com.octalsoftaware.sage.util.SagePreference;
import com.sage.android.R;
import com.sage.android.databinding.ActivityLoginBinding;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements Runnable {

    private ActivityLoginBinding mBinding;
    private String mForgotPassword;
    private String rememberMe;
    private LoginActivity mActivity;
    private Thread t1;
    private JSONObject jsonObject1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setForgotString();
        setAllHints();
        checkRemember();

        rememberMe = "false";
        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, IntroActivity.class));
            }
        });

        mBinding.cbLoginRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    rememberMe = "true";
                else
                    rememberMe = "false";
            }
        });
        mBinding.tvLoginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAnimation(mBinding.tvLoginSubmit);
                EditText etTxt[] = {mBinding.etLoginEmail, mBinding.simplePassword};
                if (isConnectingToInternet(mContext)) {
                    if (checkValidation(etTxt)) {
                        try {
                            callApi(APIRequest.login(mBinding.etLoginEmail.getText().toString(),
                                    mBinding.simplePassword.getText().toString(), rememberMe));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    warningToast(mContext, getString(R.string.no_internet_connection));
                }

                //  sendDataSinch();

            }
        });
    }

    private void sendDataSinch() {
        SinchClient sinchClient = Sinch.getSinchClientBuilder().context(mContext)
                .applicationKey("68b5aab7-b7ee-4d11-aaaa-635918eebfb8")
                .applicationSecret("mOgkSjhYZUmCKTxj5x15Pg==")
                .environmentHost("sandbox.sinch.com")
                .userId("abc1")
                .build();
        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.start();


        MessageClient messageClient = sinchClient.getMessageClient();


        WritableMessage message = new WritableMessage(
                "1",
                mBinding.etLoginEmail.getText().toString());
// Send it
        messageClient.send(message);

        messageClient.addMessageClientListener(new MessageClientListener() {
            @Override
            public void onIncomingMessage(MessageClient messageClient, Message message) {
                String ss1 = message.getSenderId();
                String ss2 = message.getTextBody();
            }

            @Override
            public void onMessageSent(MessageClient messageClient, Message message, String s) {

                String ss1 = message.getSenderId();
                sucessToast(mContext, ss1);
            }

            @Override
            public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {

            }

            @Override
            public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {

            }

            @Override
            public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {

            }
        });

        sinchClient.addSinchClientListener(new SinchClientListener() {
            public void onClientStarted(SinchClient client) {
            }

            public void onClientStopped(SinchClient client) {
            }

            public void onClientFailed(SinchClient client, SinchError error) {
            }

            public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration registrationCallback) {
            }

            public void onLogMessage(int level, String area, String message) {
                sucessToast(mContext, message);
            }
        });
    }


    private void checkRemember() {
        String rememberStatus = SagePreference.getUnit(S.REMEMBER_ME, mContext);
        String email = SagePreference.getUnit(S.EMAIL, mContext);
        String password = SagePreference.getUnit(S.PASSWORD, mContext);
        if (rememberStatus.equalsIgnoreCase("true")) {
            mBinding.etLoginEmail.setText(email);
            mBinding.simplePassword.setText(password);
        }
    }

    private void setAllHints() {
        mBinding.etLoginEmail.setHint(getString(R.string.hint_email_address));
        mBinding.simplePassword.setHint(getString(R.string.hint_password));
    }

    private void callApi(Map<String, Object> login) {
        showProgress(mContext);
        MyApiEndpointInterface myApiEndpointInterface = SageSeekerApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
        Call<ServerLogin> call = null;
        call = myApiEndpointInterface.login(login);
        call.enqueue(new Callback<ServerLogin>() {
            @Override
            public void onResponse(Call<ServerLogin> call, Response<ServerLogin> response) {
                try {
                    hideProgress();
                    /*String ss = response.body().string();
                    String ss1 = ss;*/
                    if (response.body().getStatus() == getResources().getInteger(R.integer.response_success)) {
                        Log.e("res", response.body().getData().toString());
                        JsonObject gson = new JsonParser().parse(response.body().getData().toString()).getAsJsonObject();
                        jsonObject1 = new JSONObject(gson.toString());
                        setResponseData(jsonObject1);
                    } else {
                        errorToast(mContext, response.body().getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ServerLogin> call, Throwable t) {
                hideProgress();
            }
        });

    }

    private void setResponseData(JSONObject jsonObject1) {
        try {
            if (jsonObject1 != null) {
                Log.e("seeker_id", jsonObject1.optString("user_id"));
                SagePreference.setUnit(S.STATUS_LOGIN, "true", mContext);
                SagePreference.setUnit(S.FIRST_NAME, jsonObject1.optString("first_name"), mContext);
                SagePreference.setUnit(S.LAST_NAME, jsonObject1.optString("last_name"), mContext);
                SagePreference.setUnit(S.EMAIL, jsonObject1.optString("email"), mContext);
                SagePreference.setUnit(S.PHONE, jsonObject1.optString("contact"), mContext);
                SagePreference.setUnit(S.PROFILE_URL, jsonObject1.optString("profile_image"), mContext);
                SagePreference.setUnit(S.USER_ID, jsonObject1.optString("user_id"), mContext);
                SagePreference.setUnit(S.REMEMBER_ME, rememberMe, mContext);
                SagePreference.setUnit(S.PASSWORD, mBinding.simplePassword.getText().toString().trim(), mContext);
                startActivity(new Intent(LoginActivity.this, HomeActiviy.class));
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setForgotString() {
        mForgotPassword = "Forgot Password?";
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString whiteSpannable = new SpannableString(mForgotPassword);
        whiteSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0, mForgotPassword.length(), 0);
        builder.append(whiteSpannable);
        mBinding.tvLoginForgotPassword.setText(builder, TextView.BufferType.SPANNABLE);

    }

    @Override
    public void run() {


    }
}
