package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.network.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GptApiRepository
{
    private static final int maxNumberOfRequests = 16;
    
    private String openaiCompletionsEndpoint;
    private String API_key;
    private String namedEntityRecognitionPrompt;
    private int apiResponseTimeoutMs = 50000;
    //public static final String requestTag = "NamedEntityRecognitionRequest";
    
    Context appContext;
    public static final ExecutorService apiRequestThreadPool = Executors.newFixedThreadPool(maxNumberOfRequests);
    
    public GptApiRepository(Application application)
    {
        this.appContext = application.getApplicationContext();
    
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
        this.API_key = preferences.getString("openai_API_key", "");
        if(!API_key.isEmpty())
        {
        
        }
        this.openaiCompletionsEndpoint = application.getString(R.string.openai_completions_endpoint);
        this.namedEntityRecognitionPrompt = application.getString(R.string.named_entity_recognition_prompt);
    }
    
    private JSONObject createCompletionRequestBody(String textBlock)  throws JSONException
    {
        JSONObject requestBody = new JSONObject();
        // adding params to json object.
        requestBody.put("model", "text-davinci-003");
        //requestBody.put("model", "gpt-3.5-turbo");
        String prompt = namedEntityRecognitionPrompt + textBlock + "\n\n[Output]";
        requestBody.put("prompt", prompt);
        requestBody.put("temperature", 0);
        requestBody.put("max_tokens", 1500);
        requestBody.put("top_p", 1);
        requestBody.put("frequency_penalty", 0.0);
        requestBody.put("presence_penalty", 0.0);
        return requestBody;
    }
    
    private Map<String, String> getCompletionRequestHeaders()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", " application/json");
        params.put("Authorization", "Bearer " + API_key);
        return params;
    }
    
    private JsonObjectRequest createCompletionRequest(JSONObject reqBody, ApiResponseListener listener)
    {
        // Request a string response from the provided URL.
        return new JsonObjectRequest(Request.Method.POST,
                openaiCompletionsEndpoint,
                reqBody,
                response -> listener.OnResponse(response),
                error -> listener.OnError(error))
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                return getCompletionRequestHeaders();
            }
        };
    }
    
    private void performCompletionRequest(JsonObjectRequest jsonRequest, String requestTag)
    {
        // Add the request to the RequestQueue.
        jsonRequest.setTag(requestTag);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(apiResponseTimeoutMs, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //RequestQueueSingleton.getInstance(appContext).addToRequestQueue(jsonRequest);
    
        Volley.newRequestQueue(appContext).add(jsonRequest);
    }
    
    public void getCompletion(String textBlock, String tag, ApiResponseListener responseListener) throws JSONException
    {
        apiRequestThreadPool.execute(() -> {
            JSONObject requestBody = null;
            try
            {
                requestBody = createCompletionRequestBody(textBlock);
            } catch (JSONException ex)
            {
                responseListener.OnException(ex);
            }
            JsonObjectRequest jsonRequest = createCompletionRequest(requestBody, responseListener);
            performCompletionRequest(jsonRequest, tag);
        });
    }
}
