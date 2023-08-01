package com.nimbusbg.audiobookcanvas.data.listeners;

public interface TtsUtteranceListener
{
    void OnUtteranceStart(String s);
    void OnUtteranceDone(String s);
    void OnUtteranceError(String s);
}
