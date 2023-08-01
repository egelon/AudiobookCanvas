package com.nimbusbg.audiobookcanvas.data.listeners;

import androidx.annotation.NonNull;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public interface ApiResponseListener
{
    void OnResponse(@NonNull Call call, @NonNull Response response);
    void OnError(@NonNull Call call, @NonNull IOException e);
}
