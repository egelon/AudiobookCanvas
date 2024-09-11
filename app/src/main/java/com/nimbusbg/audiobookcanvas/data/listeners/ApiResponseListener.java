package com.nimbusbg.audiobookcanvas.data.listeners;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public interface ApiResponseListener
{
    void OnResponse(@NonNull Call call, @NonNull Response response) throws IOException;
    void OnError(@NonNull Call call, @NonNull IOException e);
}
