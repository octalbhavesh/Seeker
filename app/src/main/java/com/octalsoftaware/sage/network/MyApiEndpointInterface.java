package com.octalsoftaware.sage.network;

import com.octalsoftaware.sage.response.ServerImgResponse;
import com.octalsoftaware.sage.response.ServerLogin;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface MyApiEndpointInterface {

    @POST("login")
    Call<ServerLogin> login(@Body Map<String, Object> commentRequest);

    @POST("logout")
    Call<ResponseBody> logout(@Body Map<String, Object> commentRequest);

    @POST("send_otp")
    Call<ServerLogin> register_one(@Body Map<String, Object> commentRequest);

    @Multipart
    @POST("register")
    Call<ServerImgResponse> register_two(@PartMap() Map<String, RequestBody> partMap);

    @Multipart
    @POST("register")
    Call<ServerImgResponse> register_two(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part imageFile);

    @POST("verify_otp")
    Call<ServerLogin> verify_otp(@Body Map<String, Object> commentRequest);

    @POST("category_list")
    Call<ServerLogin> categories(@Body Map<String, Object> commentRequest);

    @POST("get_consultant_list")
    Call<ServerLogin> conslantant(@Body Map<String, Object> commentRequest);

    @POST("get_consultant_search_list")
    Call<ResponseBody> search(@Body Map<String, Object> commentRequest);

    @POST("get_about_us_faq")
    Call<ResponseBody> aboutUs(@Body Map<String, Object> commentRequest);

    @POST("get_contact_us")
    Call<ResponseBody> contactUs(@Body Map<String, Object> commentRequest);

    @POST("get_consultant_details")
    Call<ServerLogin> conslantantDetail(@Body Map<String, Object> commentRequest);

    @POST("send_chat_request")
    Call<ServerLogin> sendRequest(@Body Map<String, Object> commentRequest);

}
