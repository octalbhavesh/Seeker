package com.octalsoftaware.sage.view.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.octalsoftaware.sage.SageSeekerApplication;
import com.octalsoftaware.sage.model.CategoriesModel;
import com.octalsoftaware.sage.network.APIRequest;
import com.octalsoftaware.sage.network.MyApiEndpointInterface;
import com.octalsoftaware.sage.response.ServerLogin;
import com.octalsoftaware.sage.util.Utils;
import com.octalsoftaware.sage.view.activity.HomeActiviy;
import com.octalsoftaware.sage.view.activity.SearchActivity;
import com.octalsoftaware.sage.view.adapter.CategoriesAdapter;
import com.sage.android.R;
import com.sage.android.databinding.FragmentBrowseCategoriesBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseCategoriesFragment extends android.app.Fragment {

    private FragmentBrowseCategoriesBinding mBinding;
    private ArrayList<CategoriesModel> mCategoriesList = new ArrayList<>();
    private CategoriesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_browse_categories, container, false);
        View rootView = mBinding.getRoot();
        mBinding.ivHomeDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActiviy) getActivity()).openDrawer();
            }
        });
        /*Wed Jan 02 06:54:59 GMT+00:00 2019*/

        mBinding.ivHomeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SearchActivity.class);
                startActivity(i);
               // startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
        fetchCategories();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.rvCategories.setLayoutManager(layoutManager);
        adapter = new CategoriesAdapter(getActivity(), mCategoriesList);
        mBinding.rvCategories.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    private void fetchCategories() {
        if (Utils.isConnectingToInternet(getActivity())) {
            try {
                callApi(APIRequest.categories());
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
        call = myApiEndpointInterface.categories(categories);
        call.enqueue(new Callback<ServerLogin>() {
            @Override
            public void onResponse(Call<ServerLogin> call, Response<ServerLogin> response) {
                try {
                    Utils.hideProgress();
                    if (response.body().getStatus() == getResources().getInteger(R.integer.response_success)) {
                        JsonObject gson = new JsonParser().parse(response.body().getData().toString()).getAsJsonObject();
                        JSONObject jsonObject1 = new JSONObject(gson.toString());
                        mCategoriesList.clear();
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
        JSONArray jsonArray = jsonObject1.optJSONArray("category");
        for (int i = 0; i < jsonArray.length(); i++) {
            CategoriesModel model = new CategoriesModel();
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.optString("id");
                model.setId(id);
                String title = jsonObject.optString("category_name");
                model.setName(title);
                String description = jsonObject.optString("description");
                model.setDescription(description);
                String image = jsonObject.optString("image");
                model.setImage(image);
                mCategoriesList.add(model);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Log.e("size", String.valueOf(mCategoriesList.size()));
        if (mCategoriesList.size() > 0) {
            adapter = new CategoriesAdapter(getActivity(), mCategoriesList);
            mBinding.rvCategories.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Utils.errorToast(getActivity(), getString(R.string.no_data_found));
        }
    }
   /* private void callApi() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvCategories.setLayoutManager(linearLayoutManager);
        mCategoriesList.clear();

        for (int i = 0; i < 6; i++) {
            CategoriesModel model = new CategoriesModel();
            model.setDescription("Lorem ipsum dolor sit er elit lamet, consectetaur cillium adipisicing pecu, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
            if (i == 0)
                model.setName("Love Psychics");
            else if (i == 1)
                model.setName("Psychics Reading");
            else if (i == 2)
                model.setName("Tarot Reading");
            else if (i == 3)
                model.setName("Fortune Telling");
            else if (i == 4)
                model.setName("Personal Astrology");
            else if (i == 5)
                model.setName("Dream Analysis");

            mCategoriesList.add(model);
        }
        if (mCategoriesList.size() > 0) {
            adapter = new CategoriesAdapter(getActivity(), mCategoriesList);
            mBinding.rvCategories.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }*/
}
