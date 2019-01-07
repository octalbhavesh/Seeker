package com.octalsoftaware.sage.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.octalsoftaware.sage.SageSeekerApplication;
import com.octalsoftaware.sage.constants.S;
import com.octalsoftaware.sage.model.SimpleChatModel;
import com.octalsoftaware.sage.network.APIRequest;
import com.octalsoftaware.sage.network.MyApiEndpointInterface;
import com.octalsoftaware.sage.response.ServerLogin;
import com.octalsoftaware.sage.sinch.SinchService;
import com.octalsoftaware.sage.util.SagePreference;
import com.octalsoftaware.sage.util.Utils;
import com.octalsoftaware.sage.view.activity.ConslantantDetailActivity;
import com.octalsoftaware.sage.view.activity.VideoCallingActivity;
import com.sage.android.R;
import com.sinch.android.rtc.MissingPermissionException;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoiceChatAdapter extends RecyclerView.Adapter<VoiceChatAdapter.MyViewHolder> {

    private Context mContext;
    private List<SimpleChatModel> chatList;
    private int tempPosition = -1;
    private int tempPositionClick = -1;
    public CountDownTimer mTimer = null;
    private SinchService.SinchServiceInterface mSinchServiceInterface;
    public VoiceChatAdapter(Context activity, List<SimpleChatModel> chatList) {
        mContext = activity;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_voice_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        /*if (!TextUtils.isEmpty(newsModalList.get(position).getImage()))
            Picasso.get().load(newsModalList.get(position).getImage()).placeholder(R.drawable.logo).into(holder.user_profile_pic_iv);*/
        //    Log.e("imageUrl", imagesModalList.get(position).getImgUrl());
        //  holder.tvNotification.setText(notificationList.get(position).getNotification());
        /*if (chatList.get(position).getStatus().equalsIgnoreCase("1")) {
            holder.tvStatus.setText("Online");
            holder.ivStatus.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatus.setText("Offline");
            holder.ivStatus.setVisibility(View.GONE);
        }*/
        holder.ivAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempPositionClick = position;
                sendRequest(String.valueOf(mContext.getResources().getInteger(R.integer.call_audio)));
            }
        });

        holder.ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempPositionClick = position;
                sendRequest(String.valueOf(mContext.getResources().getInteger(R.integer.call_video)));
                callVideo();
            }
        });
        /*holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ConslantantDetailActivity.class));
            }
        });
       */

        holder.tvName.setText(chatList.get(position).getName() + " " + chatList.get(position).getLastName());
        if (chatList.get(position).getDescription().length() > 100) {
            holder.tvDescription.setText(chatList.get(position).getDescription().substring(0, 99));
        } else {
            holder.tvDescription.setText(chatList.get(position).getDescription());
        }

        holder.rating.setRating((float) chatList.get(position).getRating());
        holder.tvPrice.setText("$ " + chatList.get(position).getPrice() + "/");

        try {
            if (chatList.get(position).getImage() != null) {
                Picasso.get()
                        .load(chatList.get(position).getImage())
                        .into(holder.ivIcon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ConslantantDetailActivity.class);
                i.putExtra("id", chatList.get(position).getId());
                mContext.startActivity(i);
                // mContext.startActivity(new Intent(mContext, ConslantantDetailActivity.class));
            }
        });
       /* holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempPosition = position;
                notifyDataSetChanged();

                mContext.startActivity(new Intent(mContext, ChatListingActivity.class));
            }
        });*/
        /*if (tempPosition == position) {
            holder.layoutMain.setBackgroundColor(mContext.getColor(R.color.theme_yellow));
            holder.tvName.setTextColor(mContext.getColor(R.color.colorWhite));
            holder.tvDescription.setTextColor(mContext.getColor(R.color.colorWhite));
            holder.layoutImgBackground.setBackgroundColor(mContext.getColor(R.color.theme_yellow));
            holder.ivIcon.setColorFilter(Color.parseColor("#ffffff"));
        } else {
            holder.layoutMain.setBackgroundColor(mContext.getColor(R.color.colorWhite));
            holder.tvName.setTextColor(mContext.getColor(R.color.colorBlack));
            holder.tvDescription.setTextColor(Color.parseColor("#99333333"));
            holder.layoutImgBackground.setBackgroundColor(Color.parseColor("#e9e9e9"));
            holder.ivIcon.setColorFilter(Color.parseColor("#99333333"));
        }*/
    }
    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }
    private void callVideo() {
        try {
            String callId = null;
            try {
                com.sinch.android.rtc.calling.Call call =getSinchServiceInterface().callUserVideo("42","","42","");
                callId = call.getCallId();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                try {
                    if (mTimer != null) {
                        mTimer.cancel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
           /* SexityApplication.flagChatVideo = true;
            SexityConstant.RECEIVER_ID = receiverId;*/
            Intent callScreen = new Intent(mContext, VideoCallingActivity.class);
            callScreen.putExtra(SinchService.CALL_ID, callId);
            callScreen.putExtra("tag", "outgoingCall");
            callScreen.putExtra("receiverUserImage", "");
            callScreen.putExtra("receiverUserName", "consaltant");
            callScreen.putExtra("receiverId", "42");
            callScreen.putExtra("threadId", "");
            callScreen.putExtra("walletBalance", "");
            callScreen.putExtra("videoPrice", "");
            callScreen.putExtra("receiverId", "42");
            mContext.startActivity(callScreen);
           // finish();


           /* SaveAllChatMessageResponse mSaveAllSentChatMessage = new SaveAllChatMessageResponse();
            mSaveAllSentChatMessage.setMessageId("");
            mSaveAllSentChatMessage.setMessage(receiverUserName + " Calling");
            mSaveAllSentChatMessage.setTimeStamp("");
            mSaveAllSentChatMessage.setSenderId(SexityPreferences.getUserId(ChatActivity.this));
            mSaveAllSentChatMessage.setSenderName(SexityPreferences.getUserName(ChatActivity.this));
            mSaveAllSentChatMessage.setSenderProfileImage(SexityPreferences.getProfileImage(ChatActivity.this));
            mSaveAllSentChatMessage.setReceiverId(receiverId);
            mSaveAllSentChatMessage.setReceiverName(receiverUserName);
            mSaveAllSentChatMessage.setReceiverProfileImage(receiverUserImage);
            mSaveAllSentChatMessage.setChatType("Call");
            mSaveAllSentChatMessage.setCallType("");*/

        } catch (MissingPermissionException e) {
           // ActivityCompat.requestPermissions(, new String[]{e.getRequiredPermission()}, 0);
        }
    }
    private void sendRequest(String type) {
        if (Utils.isConnectingToInternet(mContext)) {
            try {
                sendRequestApi(APIRequest.sendRequest(SagePreference.getUnit(S.USER_ID, mContext),
                        chatList.get(tempPositionClick).getId(), type));
                //  callApi(APIRequest.conslantantDetail("32"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Utils.warningToast(mContext, mContext.getString(R.string.no_internet_connection));
        }
    }

    private void sendRequestApi(Map<String, Object> detail) {
        Utils.showProgress(mContext);
        MyApiEndpointInterface myApiEndpointInterface = SageSeekerApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
        Call<ServerLogin> call = null;
        call = myApiEndpointInterface.sendRequest(detail);
        call.enqueue(new Callback<ServerLogin>() {
            @Override
            public void onResponse(Call<ServerLogin> call, Response<ServerLogin> response) {
                try {
                    Utils.hideProgress();
                    if (response.body().getStatus() == mContext.getResources().getInteger(R.integer.response_success)) {
                        JsonObject gson = new JsonParser().parse(response.body().getData().toString()).getAsJsonObject();
                        Utils.sucessToast(mContext, response.body().getMessage());
                    } else {
                        Utils.errorToast(mContext, response.body().getMessage());
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<ServerLogin> call, Throwable t) {
                Utils.hideProgress();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvName, tvDescription, tvPrice;
        ImageView ivVideo, ivAudio, ivStatus, ivIcon;
        LinearLayout layoutMain, layoutImgBackground;
        AppCompatRatingBar rating;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvPrice = itemView.findViewById(R.id.tv_chat_price);
            tvName = itemView.findViewById(R.id.tv_chat_name);
            ivVideo = itemView.findViewById(R.id.iv_video);
            ivAudio = itemView.findViewById(R.id.iv_audio);
            ivStatus = itemView.findViewById(R.id.iv_voice_chat_status);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDescription = itemView.findViewById(R.id.tv_chat_description);
            layoutMain = itemView.findViewById(R.id.layout_chat_voice_main);
            rating = itemView.findViewById(R.id.rating);
        }
    }
}
