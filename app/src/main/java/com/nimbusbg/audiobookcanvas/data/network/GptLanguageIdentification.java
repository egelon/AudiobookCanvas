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
            case "en": {this.dialogue_start = '“'; break;}
            case "fr": {this.dialogue_start = '«'; break;}
            case "de": {this.dialogue_start = '„'; break;}
            case "es": {this.dialogue_start = '«'; break;}
            case "pt": {this.dialogue_start = '—'; break;}
            case "it": {this.dialogue_start = '«'; break;}
            case "ru": {this.dialogue_start = '«'; break;}
            case "pl": {this.dialogue_start = '„'; break;}
            case "ja": {this.dialogue_start = '「'; break;}
            case "zh": {this.dialogue_start = '“'; break;}
            case "ko": {this.dialogue_start = '「'; break;}
            case "bg": {this.dialogue_start = '—'; break;}
            case "el": {this.dialogue_start = '«'; break;}
            case "sr": {this.dialogue_start = '„'; break;}
            case "da": {this.dialogue_start = '»'; break;}
            case "sv": {this.dialogue_start = '”'; break;}
            case "fi": {this.dialogue_start = '”'; break;}
            case "no": {this.dialogue_start = '«'; break;}
            case "nl": {this.dialogue_start = '„'; break;}
            case "he": {this.dialogue_start = '„'; break;}
        }
        return this.dialogue_start;
    }

    public char getDialogue_end()
    {
        switch(this.language)
        {
            case "en": {this.dialogue_end = '”'; break;}
            case "fr": {this.dialogue_end = '»'; break;}
            case "de": {this.dialogue_end = '“'; break;}
            case "es": {this.dialogue_end = '»'; break;}
            case "pt": {this.dialogue_end = '—'; break;}
            case "it": {this.dialogue_end = '»'; break;}
            case "ru": {this.dialogue_end = '»'; break;}
            case "pl": {this.dialogue_end = '”'; break;}
            case "ja": {this.dialogue_end = '」'; break;}
            case "zh": {this.dialogue_end = '”'; break;}
            case "ko": {this.dialogue_end = '」'; break;}
            case "bg": {this.dialogue_end = '—'; break;}
            case "el": {this.dialogue_end = '»'; break;}
            case "sr": {this.dialogue_end = '”'; break;}
            case "da": {this.dialogue_end = '«'; break;}
            case "sv": {this.dialogue_end = '”'; break;}
            case "fi": {this.dialogue_end = '”'; break;}
            case "no": {this.dialogue_end = '»'; break;}
            case "nl": {this.dialogue_end = '”'; break;}
            case "he": {this.dialogue_end = '”'; break;}
        }
        return this.dialogue_end;
    }
}
