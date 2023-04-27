package com.example.audiobookcanvas;

public class BackgroundMusicModel {
    private int trackId;
    private AtmosphereModel atmosphere;
    private int volume;
    private String path;

    public BackgroundMusicModel(int trackId, AtmosphereModel atmosphere, int volume, String path) {
        this.trackId = trackId;
        this.atmosphere = atmosphere;
        this.volume = volume;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        if(volume >0 && volume <=100){
            this.volume = volume;
        }
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public AtmosphereModel getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(AtmosphereModel atmosphere) {
        this.atmosphere = atmosphere;
    }
}
