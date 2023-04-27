package com.example.audiobookcanvas;

import java.util.List;

public class ContentModel {
    private String textFileName;
    private List<TextBlockModel> textBlocks;
    private AudioModel audio;

    public ContentModel(String textFileName, List<TextBlockModel> textBlocks, AudioModel audio) {
        this.textFileName = textFileName;
        this.textBlocks = textBlocks;
        this.audio = audio;
    }

    public String getTextFileName() {
        return textFileName;
    }

    public void setTextFileName(String textFileName) {
        this.textFileName = textFileName;
    }

    public List<TextBlockModel> getTextBlocks() {
        return textBlocks;
    }

    public void setTextBlocks(List<TextBlockModel> textBlocks) {
        this.textBlocks = textBlocks;
    }

    public AudioModel getAudio() {
        return audio;
    }

    public void setAudio(AudioModel audio) {
        this.audio = audio;
    }
}
