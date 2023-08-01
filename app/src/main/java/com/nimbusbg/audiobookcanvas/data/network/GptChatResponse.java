package com.nimbusbg.audiobookcanvas.data.network;

import java.util.List;

public class GptChatResponse
{
    private String id;
    private String object;
    private long created;
    private List<GptChatChoice> choices;
    private GptUsage usage;
    private GptError error;
    
    
    // Add getters and setters.
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getObject() {
        return object;
    }
    
    public void setObject(String object) {
        this.object = object;
    }
    
    public long getCreated() {
        return created;
    }
    
    public void setCreated(long created) {
        this.created = created;
    }
    
    public List<GptChatChoice> getChoices() {
        return choices;
    }
    
    public void setChoices(List<GptChatChoice> choices) {
        this.choices = choices;
    }
    
    public GptUsage getUsage() {
        return usage;
    }
    
    public void setUsage(GptUsage usage) {
        this.usage = usage;
    }
    
    public GptError getError()
    {
        return error;
    }
    
    public void setError(GptError error)
    {
        this.error = error;
    }
    
    public boolean hasError()
    {
        return error != null;
    }
}
