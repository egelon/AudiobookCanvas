package com.example.audiobookcanvas;

import java.util.List;
import java.util.Stack;

public class TextBlockModel {
    private int index;
    private String text;
    private List<VoiceLineModel> characterLines;
    private Stack<PreviousStateModel> history;
    private AtmosphereModel atmosphere;
    private String generatedAudioPath;

    public TextBlockModel(int index, String text, List<VoiceLineModel> characterLines, Stack<PreviousStateModel> history, AtmosphereModel atmosphere, String generatedAudioPath) {
        this.index = index;
        this.text = text;
        this.characterLines = characterLines;
        this.history = history;
        this.atmosphere = atmosphere;
        this.generatedAudioPath = generatedAudioPath;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<VoiceLineModel> getCharacterLines() {
        return characterLines;
    }

    public void setCharacterLines(List<VoiceLineModel> characterLines) {
        this.characterLines = characterLines;
    }

    public Stack<PreviousStateModel> getHistory() {
        return history;
    }

    public void setHistory(Stack<PreviousStateModel> history) {
        this.history = history;
    }

    public AtmosphereModel getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(AtmosphereModel atmosphere) {
        this.atmosphere = atmosphere;
    }

    public String getGeneratedAudioPath() {
        return generatedAudioPath;
    }

    public void setGeneratedAudioPath(String generatedAudioPath) {
        this.generatedAudioPath = generatedAudioPath;
    }
}
