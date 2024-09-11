package com.nimbusbg.audiobookcanvas.data.network;

public class GptLanguageIdentification
{
    private String language;
    private char dialogue_start;
    private char dialogue_end;
    
    public GptLanguageIdentification(String language, char dialogue_start, char dialogue_end)
    {
        this.language = language;
        this.dialogue_start = dialogue_start;
        this.dialogue_end = dialogue_end;
    }
    
    public String getLanguage()
    {
        return language;
    }
    
    public void setLanguage(String language)
    {
        this.language = language;
    }
    
    public char getDialogue_start()
    {
        switch(this.language)
        {
            case "en_US": {this.dialogue_start = '\u201C'; break;}
            case "en_GB": {this.dialogue_start = '\u201C'; break;}
            case "fr_FR": {this.dialogue_start = '\u00AB'; break;}
            case "de_DE": {this.dialogue_start = '\u201E'; break;}
            case "es_ES": {this.dialogue_start = '\u00AB'; break;}
            case "pt_PT": {this.dialogue_start = '\u2014'; break;}
            case "it_IT": {this.dialogue_start = '\u00AB'; break;}
            case "ru_RU": {this.dialogue_start = '\u00AB'; break;}
            case "bg_BG": {this.dialogue_start = '\u2014'; break;}
            case "el_GR": {this.dialogue_start = '\u00AB'; break;}
        }
        return this.dialogue_start;
    }

    public char getDialogue_end()
    {
        switch(this.language)
        {
            case "en_US": {this.dialogue_end = '\u201D'; break;}
            case "en_GB": {this.dialogue_end = '\u201D'; break;}
            case "fr_FR": {this.dialogue_end = '\u00BB'; break;}
            case "de_DE": {this.dialogue_end = '\u201C'; break;}
            case "es_ES": {this.dialogue_end = '\u00BB'; break;}
            case "pt_PT": {this.dialogue_end = '\u2014'; break;}
            case "it_IT": {this.dialogue_end = '\u00BB'; break;}
            case "ru_RU": {this.dialogue_end = '\u00BB'; break;}
            case "bg_BG": {this.dialogue_end = '\u2014'; break;}
            case "el_GR": {this.dialogue_end = '\u00BB'; break;}
        }
        return this.dialogue_end;
    }
}
