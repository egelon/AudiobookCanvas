package com.nimbusbg.audiobookcanvas.data.network;

import java.util.List;

public class GptCompletion
{
    List<GptCharacterLine> characterLines;
    List<GptCharacter> characters;
    
    public GptCompletion(List<GptCharacterLine> characterLines, List<GptCharacter> characters)
    {
        this.characterLines = characterLines;
        this.characters = characters;
    }
    
    public List<GptCharacterLine> getCharacterLines()
    {
        return characterLines;
    }
    
    public void setCharacterLines(List<GptCharacterLine> characterLines)
    {
        this.characterLines = characterLines;
    }
    
    public List<GptCharacter> getCharacters()
    {
        return characters;
    }
    
    public void setCharacters(List<GptCharacter> characters)
    {
        this.characters = characters;
    }
}
