package com.example.audiobookcanvas;

public class VoiceLineModel {
    private int lineId;
    private int startIndex;
    private String characterName;

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public VoiceLineModel(int lineId, int startIndex, String characterName) {
        this.lineId = lineId;
        this.startIndex = startIndex;
        this.characterName = characterName;
    }
}
