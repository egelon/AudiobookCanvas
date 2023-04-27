package com.example.audiobookcanvas;

import java.util.List;

public class AudioModel {
    private List<CharacterModel> characters;
    private List<BackgroundMusicModel> backgroundMusicTracks;

    // Constructors, getters, and setters
    public AudioModel(List<CharacterModel> characters, List<BackgroundMusicModel> backgroundMusicTracks)
    {
        this.characters = characters;
        this.backgroundMusicTracks = backgroundMusicTracks;
    }


    public List<CharacterModel> getCharacters() {
        return characters;
    }

    public void setCharacters(List<CharacterModel> characters) {
        this.characters = characters;
    }

    public List<BackgroundMusicModel> getBackgroundMusicTracks() {
        return backgroundMusicTracks;
    }

    public void setBackgroundMusicTracks(List<BackgroundMusicModel> backgroundMusicTracks) {
        this.backgroundMusicTracks = backgroundMusicTracks;
    }

    public CharacterModel getCharacterByName(String characterName)
    {
        if(this.characters.isEmpty())
        {
            return null;
        }
        else
        {
            for(CharacterModel character : this.characters ) {
                if (character.getName() == characterName) {
                    return character;
                }
            }
            return null;
        }
    }

    public BackgroundMusicModel getMusicByAtmosphere(AtmosphereModel atmosphere)
    {
        if(this.backgroundMusicTracks.isEmpty())
        {
            return null;
        }
        else
        {
            for(BackgroundMusicModel backgroundMusicTrack : this.backgroundMusicTracks ) {
                if (backgroundMusicTrack.getAtmosphere() == atmosphere) {
                    return backgroundMusicTrack;
                }
            }
            return null;
        }
    }

    public BackgroundMusicModel getMusicByPrimaryAtmosphere(Atmosphere atmosphere)
    {
        if(this.backgroundMusicTracks.isEmpty())
        {
            return null;
        }
        else
        {
            for(BackgroundMusicModel backgroundMusicTrack : this.backgroundMusicTracks ) {
                if (backgroundMusicTrack.getAtmosphere().getPrimaryAtmosphere() == atmosphere) {
                    return backgroundMusicTrack;
                }
            }
            return null;
        }
    }

    public BackgroundMusicModel getMusicBySecondaryAtmosphere(Atmosphere atmosphere)
    {
        if(this.backgroundMusicTracks.isEmpty())
        {
            return null;
        }
        else
        {
            for(BackgroundMusicModel backgroundMusicTrack : this.backgroundMusicTracks ) {
                if (backgroundMusicTrack.getAtmosphere().getSecondaryAtmosphere() == atmosphere) {
                    return backgroundMusicTrack;
                }
            }
            return null;
        }
    }
}

