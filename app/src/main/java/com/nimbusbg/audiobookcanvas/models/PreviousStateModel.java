package com.nimbusbg.audiobookcanvas.models;

public class PreviousStateModel {
    private int historyId;
    private VoiceLineModel previousVoiceLine;

    public PreviousStateModel(int historyId, VoiceLineModel previousVoiceLine) {
        this.historyId = historyId;
        this.previousVoiceLine = previousVoiceLine;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public VoiceLineModel getPreviousVoiceLine() {
        return previousVoiceLine;
    }

    public void setPreviousVoiceLine(VoiceLineModel previousVoiceLine) {
        this.previousVoiceLine = previousVoiceLine;
    }
}
