package com.octalsoftaware.sage.network;

import com.google.firebase.iid.FirebaseInstanceId;
import com.octalsoftaware.sage.util.ConvertJsonToMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class APIRequest {

    public static Map<String, Object> login(String email, String password, String remember) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        jsonObject.put("remember_me", remember);
        jsonObject.put("device_token", FirebaseInstanceId.getInstance().getToken());
        jsonObject.put("device_type", "android");
        return new ConvertJsonToMap().jsonToMap(jsonObject);
    }

    public static Map<String, Object> registerOne(String countryCode, String phoneNumber) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("country_code", countryCode);
        jsonObject.put("contact", phoneNumber);
        return new ConvertJsonToMap().jsonToMap(jsonObject);
    }

    public static Map<String, Object> verifyOtp(String otp, String phoneNumber) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("otp", otp);
        jsonObject.put("contact", phoneNumber);
        return new ConvertJsonToMap().jsonToMap(jsonObject);
    }

    public static Map<String, Object> conslantant(String id) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("category_id", id);
        return new ConvertJsonToMap().jsonToMap(jsonObject);
    }

    public static Map<String, Object> aboutUs(String type) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        return new ConvertJsonToMap().jsonToMap(jsonObject);
    }

    public static Map<String, Object> logout(String userId) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);
        return new ConvertJsonToMap().jsonToMap(jsonObject);
    }

    public static Map<String, Object> search(String userId, String keyword) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);
        jsonObject.put("keyword", keyword);
        return new ConvertJsonToMap().jsonToMap(jsonObject);
    }

    public static Map<String, Object> contactUs(String type) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        return new ConvertJsonToMap().jsonToMap(jsonObject);
    }


    public static Map<String, Object> conslantantDetail(String id) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("consultant_id", id);
        return new ConvertJsonToMap().jsonToMap(jsonObject);
    }


    public static Map<String, Object> sendRequest(String senderId, String reciverId, String type) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender_id", senderId);
        jsonObject.put("reciver_id", reciverId);
        jsonObject.put("type", type);
        return new ConvertJsonToMap().jsonToMap(jsonObject);
    }

    public static Map<String, Object> categories() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        return new ConvertJsonToMap().jsonToMap(jsonObject);
    }

    public static RequestBody registerTwo(String userId, String first_name, String last_name,
                                          String email, String password, String device_token) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);
        jsonObject.put("first_name", first_name);
        jsonObject.put("last_name", last_name);
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        jsonObject.put("device_token", FirebaseInstanceId.getInstance().getToken());
        jsonObject.put("device_type", "android");
        jsonObject.put("role", "2");
        return RequestBody.create(MediaType.parse("multipart/form-data"), jsonObject.toString());
    }


}