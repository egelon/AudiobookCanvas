package com.nimbusbg.audiobookcanvas.data.repository;

import java.util.ArrayList;

public interface TtsListener
{
    void OnInitSuccess();
    void OnInitFailure();
    
    void OnUtteranceStart(String s);
    void OnUtteranceDone(String s);
    void OnUtteranceError(String s);
}
