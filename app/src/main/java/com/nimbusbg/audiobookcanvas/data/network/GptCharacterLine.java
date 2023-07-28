package com.nimbusbg.audiobookcanvas.data.network;

public class GptCharacterLine
{
    String line;
    String character;
    
    public String getLine()
    {
        return line;
    }
    
    public void setLine(String line)
    {
        this.line = line;
    }
    
    public String getCharacter()
    {
        return character;
    }
    
    public void setCharacter(String character)
    {
        this.character = character;
    }
    
    public GptCharacterLine(String line, String character)
    {
        this.line = line;
        this.character = character;
    }
}
