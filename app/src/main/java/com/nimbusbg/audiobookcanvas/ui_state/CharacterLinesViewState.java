package com.nimbusbg.audiobookcanvas.ui_state;

import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;

import java.util.List;

public class CharacterLinesViewState
{
    private List<String> storyCharacterNames;
    private String[] textLines;
    private List<CharacterLine> characterLines;
    
    public CharacterLinesViewState(List<String> storyCharacterNames, String[] textLines, List<CharacterLine> characterLines)
    {
        this.storyCharacterNames = storyCharacterNames;
        this.textLines = textLines;
        this.characterLines = characterLines;
    }
    
    public List<String> getStoryCharacterNames()
    {
        return storyCharacterNames;
    }
    
    public void setStoryCharacterNames(List<String> storyCharacterNames)
    {
        this.storyCharacterNames = storyCharacterNames;
    }
    
    public String[] getTextLines()
    {
        return textLines;
    }
    
    public void setTextLines(String[] textLines)
    {
        this.textLines = textLines;
    }
    
    public List<CharacterLine> getCharacterLines()
    {
        return characterLines;
    }
    
    public void setCharacterLines(List<CharacterLine> characterLines)
    {
        this.characterLines = characterLines;
    }
    
    public void addCharacterName(String name)
    {
        storyCharacterNames.add(name);
    }
}
