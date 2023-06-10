package com.nimbusbg.audiobookcanvas.data.repository;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public interface ApiResponseListener
{
    void OnResponse(JSONObject response);
    void OnError(VolleyError error);
    void OnException(JSONException ex);
}
