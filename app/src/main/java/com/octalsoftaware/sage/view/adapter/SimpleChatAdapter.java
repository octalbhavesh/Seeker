package com.octalsoftaware.sage.view.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.octalsoftaware.sage.util.SagePreference;
import com.octalsoftaware.sage.util.Utils;
import com.octalsoftaware.sage.view.activity.ChatDetailActivity;
import com.octalsoftaware.sage.view.activity.ConslantantDetailActivity;
import com.sage.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpleChatAdapter extends RecyclerView.Adapter<SimpleChatAdapter.MyViewHolder> {

    private Context mContext;
    private List<SimpleChatModel> chatList;
    private int tempPosition = -1;
    private int tempPositionClick;

    public SimpleChatAdapter(Context activity, List<SimpleChatModel> chatList) {
        mContext = activity;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_simple_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

       /* if (chatList.get(position).getStatus().equalsIgnoreCase("1")) {
            holder.tvStatus.setText("Online");
            holder.ivStatus.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatus.setText("Offline");
            holder.ivStatus.setVisibility(View.GONE);
        }*/
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
                String ss = chatList.get(position).getId();
                Intent i = new Intent(mContext, ConslantantDetailActivity.class);
                i.putExtra("id", ss);
                mContext.startActivity(i);
            }
        });
        holder.ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempPositionClick = position;
                sendRequest();

                // mContext.startActivity(new Intent(mContext, ChatDetailActivity.class));
            }
        });

    }

    private void sendRequest() {
        if (Utils.isConnectingToInternet(mContext)) {
            try {
                sendRequestApi(APIRequest.sendRequest(SagePreference.getUnit(S.USER_ID, mContext),
                        chatList.get(tempPositionClick).getId(),
                        String.valueOf(mContext.getResources().getInteger(R.integer.call_txt))));
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
                        String ss = chatList.get(tempPositionClick).getId();
                        Intent i = new Intent(mContext, ChatDetailActivity.class);
                        i.putExtra("id", ss);
                        i.putExtra("first_name", chatList.get(tempPositionClick).getName());
                        mContext.startActivity(i);
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
        TextView tvStatus, tvDescription, tvName, tvPrice;
        ImageView ivStatus, ivMessage, ivIcon;
        LinearLayout layoutMain, layoutImgBackground;
        AppCompatRatingBar rating;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvDescription = itemView.findViewById(R.id.tv_chat_description);
            tvPrice = itemView.findViewById(R.id.tv_chat_price);
            rating = itemView.findViewById(R.id.rating);
            layoutMain = itemView.findViewById(R.id.layout_simple_chat_main);
            ivMessage = itemView.findViewById(R.id.iv_chat_message);
          /*  ivStatus = itemView.findViewById(R.id.iv_simple_chat_status);
            ivMessage = itemView.findViewById(R.id.iv_chat_message);
            */
        }
    }
}
