package com.nimbusbg.audiobookcanvas.data.network;

public class GptCharacter
{
    String character;
    String gender;
    
    public GptCharacter(String character, String name)
    {
        this.character = character;
        this.gender = name;
    }
    
    public String getCharacter()
    {
        return character;
    }
    
    public void setCharacter(String character)
    {
        this.character = character;
    }
    
    public String getGender()
    {
        return gender;
    }
    
    public void setGender(String gender)
    {
        this.gender = gender;
    }
}
