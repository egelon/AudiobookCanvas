package com.nimbusbg.audiobookcanvas.models;

enum Gender {
    MALE,
    FEMALE
}

public class CharacterModel {
        private String name;
        private Gender gender;
        private String voice;

        public CharacterModel(String name, Gender gender, String voice)
        {
            this.name = name;
            this.gender = gender;
            this.voice = voice;
        }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }
}
