package com.octalsoftaware.sage.view.fragment;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octalsoftaware.sage.SageSeekerApplication;
import com.octalsoftaware.sage.network.APIRequest;
import com.octalsoftaware.sage.network.MyApiEndpointInterface;
import com.octalsoftaware.sage.util.Utils;
import com.octalsoftaware.sage.view.activity.HomeActiviy;
import com.sage.android.R;
import com.sage.android.databinding.FragmentAboutUsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AboutUsFragment extends Fragment {
    private FragmentAboutUsBinding mBinding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about_us, container, false);
        View rootView = mBinding.getRoot();
        mBinding.ivHomeDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActiviy) getActivity()).openDrawer();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.isConnectingToInternet(getActivity())) {
            try {
                callApi(APIRequest.aboutUs("1"));
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
        Call<ResponseBody> call = null;
        call = myApiEndpointInterface.aboutUs(categories);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.hideProgress();
                    String response1 = response.body().string();
                    JSONObject jsonObject = new JSONObject(response1);
                    if (jsonObject.optInt("status_code") == getResources().getInteger(R.integer.response_success)) {
                        setResponseData(jsonObject.optString("data"));
                    } else {
                        Utils.errorToast(getActivity(), jsonObject.optString("message"));
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

    private void setResponseData(String jsonObject1) {
        mBinding.wvAbout.getSettings().setJavaScriptEnabled(true);
        mBinding.wvAbout.loadDataWithBaseURL("", jsonObject1, "text/html", "UTF-8", "");
    }
}
