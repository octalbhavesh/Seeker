package com.octalsoftaware.sage.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.octalsoftaware.sage.SageSeekerApplication;
import com.octalsoftaware.sage.constants.S;
import com.octalsoftaware.sage.model.SimpleChatModel;
import com.octalsoftaware.sage.network.APIRequest;
import com.octalsoftaware.sage.network.MyApiEndpointInterface;
import com.octalsoftaware.sage.util.SagePreference;
import com.octalsoftaware.sage.util.Utils;
import com.octalsoftaware.sage.view.adapter.SimpleChatAdapter;
import com.sage.android.R;
import com.sage.android.databinding.ActivitySearchBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity {
    private ActivitySearchBinding mBinding;
    private ArrayList<SimpleChatModel> mChatList = new ArrayList<>();
    private SimpleChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.ivSearchName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnectingToInternet(mContext)) {

                    if (TextUtils.isEmpty(mBinding.etSearchName.getText().toString()))
                        errorToast(mContext, "Enter consaltant name");
                    else {
                        try {
                            callApi(APIRequest.search(SagePreference.getUnit(S.USER_ID, mContext), mBinding.etSearchName.getText().toString().trim()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    warningToast(mContext, getString(R.string.no_internet_connection));
                }

            }
        });
    }

    private void callApi(Map<String, Object> categories) {
        Utils.showProgress(mContext);
        MyApiEndpointInterface myApiEndpointInterface = SageSeekerApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
        Call<ResponseBody> call = null;
        call = myApiEndpointInterface.search(categories);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.hideProgress();
                    String response1 = response.body().string();
                    JSONObject jsonObject=new JSONObject(response1);
                    if (jsonObject.optInt("status_code") == getResources().getInteger(R.integer.response_success)) {
                        JSONArray jsonObject1 = jsonObject.optJSONArray("data");
                        mChatList.clear();
                        setResponseData(jsonObject1);
                    } else {
                        Utils.errorToast(mContext, jsonObject.optString("message"));
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.hideProgress();
            }
        });
    }

    private void setResponseData(JSONArray jsonObject1) {
        for (int i = 0; i < jsonObject1.length(); i++) {
            SimpleChatModel model = new SimpleChatModel();
            try {
                JSONObject jsonObject = jsonObject1.getJSONObject(i);
                String id = jsonObject.optString("id");
                model.setId(id);
                String name = jsonObject.optString("first_name");
                model.setName(name);
                String lastName = jsonObject.optString("last_name");
                model.setLastName(lastName);
                String description = jsonObject.optString("bio");
                model.setDescription(description);
                String image = jsonObject.optString("image");
                model.setImage(image);
                String price = jsonObject.optString("price");
                model.setPrice(price);
                double rating = jsonObject.optDouble("rating");
                model.setRating(rating);
                mChatList.add(model);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("sizeSearch", String.valueOf(mChatList.size()));
        if (mChatList.size() > 0) {
            adapter = new SimpleChatAdapter(mContext, mChatList);
            mBinding.rvChatSimple.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Utils.errorToast(mContext, getString(R.string.no_data_found));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvChatSimple.setLayoutManager(linearLayoutManager);
    }
}
