package com.octalsoftaware.sage.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.octalsoftaware.sage.constants.S;
import com.octalsoftaware.sage.database.DataBase;
import com.octalsoftaware.sage.database.MessageDetail;
import com.octalsoftaware.sage.model.LiveChatModel;
import com.octalsoftaware.sage.sinch.SinchService;
import com.octalsoftaware.sage.util.SagePreference;
import com.octalsoftaware.sage.view.adapter.LiveChatAdapter;
import com.sage.android.R;
import com.sage.android.databinding.ActivityChatDetailBinding;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatDetailActivity extends BaseActivity implements MessageClientListener, SinchService.StartFailedListener {
    private ActivityChatDetailBinding mBinding;
    private ArrayList<LiveChatModel> mChatList = new ArrayList<>();
    private LiveChatAdapter adapter;
    private String tag = "text";
    private String enviroment = "sandbox.sinch.com";
    private DataBase dataBase;
    private SinchClient sinchClient;
    private MessageClient messageClient;
    private WritableMessage message;
    List<MessageDetail> messageList;
    private String messageType;
    private String receiverDate;
    private String consaltantId, consaltantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_detail);
        dataBase = DataBase.getInMemoryDatabase(getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mBinding.rvChatTxt.setLayoutManager(layoutManager);
        mBinding.ivHomeDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.ivChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBinding.etChatMessage.getText().toString().trim().equalsIgnoreCase("")) {
                    errorToast(mContext, "enter message");
                } else {
                    sendMessage(mBinding.etChatMessage.getText().toString(), tag);
                }
            }
        });
    }

    private void sendMessage(String textBody, String tag) {
        /* seekerId=42
         * receivername=test
         * */
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM hh:mm");
        String currentDateandTime = sdf.format(new Date());
        /*getSinchServiceInterface().sendMessage("42", textBody, "40",
                "tets", "", "42", "test", "", "", "", "", currentDateandTime,SagePreference.getUnit(S.PROFILE_URL,mContext));*/

        getSinchServiceInterface().sendMessage(consaltantId, textBody, SagePreference.getUnit(S.USER_ID, mContext),
                consaltantName, "", consaltantId, consaltantName, "", "", "", "", currentDateandTime, SagePreference.getUnit(S.PROFILE_URL, mContext));
        mBinding.etChatMessage.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            consaltantId = getIntent().getExtras().getString("id");
            consaltantName = getIntent().getExtras().getString("first_name");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onServiceConnected() {
        System.out.println("sinch Connected");
        getSinchServiceInterface().setStartListener(ChatDetailActivity.this);
        if (!getSinchServiceInterface().isStarted()) {
            /* getSinchServiceInterface().startClient("40");*/
            getSinchServiceInterface().startClient(SagePreference.getUnit(S.USER_ID, mContext));
        } else {
            System.out.print("Connection start");
        }
        try {
            getSinchServiceInterface().addMessageClientListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartFailed(SinchError error) {
        System.out.println("sinch error   " + error.getMessage().toString());
    }

    @Override
    public void onStarted() {
        System.out.println("sinch started");
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().removeMessageClientListener(ChatDetailActivity.this);
        }
        super.onDestroy();
    }

    @Override
    public void onIncomingMessage(MessageClient messageClient, Message message) {
        System.out.println("sinch InComing Message");
        if (message.getHeaders().size() > 0) {
            messageType = "incoming";
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM hh:mm");
            receiverDate = sdf.format(message.getTimestamp());
            saveMessage(message);
        }

    }

    @Override
    public void onMessageSent(MessageClient messageClient, Message message, String s) {
        System.out.println("sinch Sent Message");
        if (message.getHeaders().size() > 0) {
            messageType = "sent";
            saveMessage(message);
        }
    }

    @Override
    public void onMessageFailed(MessageClient messageClient, Message message, MessageFailureInfo messageFailureInfo) {
        System.out.println("sinch Failed Message");
    }

    @Override
    public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {
        System.out.println("sinch Delivered Message");
    }

    @Override
    public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> list) {
        System.out.println("sinch Send Push Data");
    }

    private void saveMessage(Message message) {

        MessageDetail messageDetail = new MessageDetail();
        messageDetail.sender_id = message.getHeaders().get("seekerId");
        messageDetail.receiver_id = message.getHeaders().get("consaltantId");
        messageDetail.sender_name = message.getHeaders().get("seekerName");
        messageDetail.receiver_name = message.getHeaders().get("consaltantName");
        messageDetail.text_body = message.getHeaders().get("message");
        messageDetail.device_type = message.getHeaders().get("device_type");
        messageDetail.type = messageType;
        messageDetail.sender_time = message.getHeaders().get("senderDate");
        messageDetail.receiver_time = receiverDate;
        // messageDetail.time=
        dataBase.employDao().insertEmploy(messageDetail);

        messageList = dataBase.employDao().fetchAllMessage();
        mChatList.clear();
        for (int i = 0; i < messageList.size(); i++) {
            LiveChatModel model = new LiveChatModel();
            model.setSender_id(messageList.get(i).sender_id);
            model.setReceiver_id(messageList.get(i).receiver_id);
            model.setSender_message(messageList.get(i).text_body);
            model.setMessageType(messageList.get(i).type);
            model.setSender_date(messageList.get(i).sender_time);
            model.setReceiver_date(messageList.get(i).receiver_time);
            mChatList.add(model);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
            if (mChatList.size() > 0 && mChatList != null) {
                mBinding.rvChatTxt.scrollToPosition(mChatList.size() - 1);
            }
        } else {
            adapter = new LiveChatAdapter(mContext, mChatList);
            mBinding.rvChatTxt.setAdapter(adapter);
            mBinding.rvChatTxt.scrollToPosition(mChatList.size() - 1);
        }
       /* if (mChatList.size() > 0) {
            Log.e("arrayListSize", String.valueOf(mChatList));
            adapter = new LiveChatAdapter(mContext, mChatList);
            mBinding.rvChatTxt.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }*/
    }
}
