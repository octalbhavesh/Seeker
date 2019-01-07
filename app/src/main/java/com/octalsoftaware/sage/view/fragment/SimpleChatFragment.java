package com.octalsoftaware.sage.view.fragment;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.octalsoftaware.sage.view.adapter.SimpleChatAdapter;
import com.sage.android.R;
import com.sage.android.databinding.FragmentSimpleChatBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpleChatFragment extends Fragment {
    private FragmentSimpleChatBinding mBinding;
    private ArrayList<SimpleChatModel> mChatList = new ArrayList<>();
    private SimpleChatAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_simple_chat, container, false);
        View rootView = mBinding.getRoot();
        fetchConslantant();
        return rootView;
    }

    private void fetchConslantant() {
        if (Utils.isConnectingToInternet(getActivity())) {
            try {
                callApi(APIRequest.conslantant(SagePreference.getUnit(S.CATEGORY_ID, getActivity())));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Utils.warningToast(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void callApi(Map<String, Object> categories) {
        Utils.showProgress(getActivity());
        MyApiEndpointInterface myApiEndpointInterface = SageSeekerApplication.getInstance().getRequestQueue().create(MyApiEndpointInterface.class);
        Call<ServerLogin> call = null;
        call = myApiEndpointInterface.conslantant(categories);
        call.enqueue(new Callback<ServerLogin>() {
            @Override
            public void onResponse(Call<ServerLogin> call, Response<ServerLogin> response) {
                try {
                    Utils.hideProgress();
                    if (response.body().getStatus() == getResources().getInteger(R.integer.response_success)) {
                        JsonObject gson = new JsonParser().parse(response.body().getData().toString()).getAsJsonObject();
                        JSONObject jsonObject1 = new JSONObject(gson.toString());
                        mChatList.clear();
                        setResponseData(jsonObject1);
                    } else {
                        Utils.errorToast(getActivity(), response.body().getMessage());
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

    private void setResponseData(JSONObject jsonObject1) {
        JSONArray jsonArray=jsonObject1.optJSONArray("text_chat");
        for (int i = 0; i < jsonArray.length(); i++) {
            SimpleChatModel model = new SimpleChatModel();
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
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
        Log.e("size", String.valueOf(mChatList.size()));
        if (mChatList.size() > 0) {
            adapter = new SimpleChatAdapter(getActivity(), mChatList);
            mBinding.rvChatSimple.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Utils.errorToast(getActivity(), getString(R.string.no_data_found));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvChatSimple.setLayoutManager(linearLayoutManager);
    }
}
