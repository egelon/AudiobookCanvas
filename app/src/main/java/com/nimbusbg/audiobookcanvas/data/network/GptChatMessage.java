package com.nimbusbg.audiobookcanvas.data.network;

public class GptChatMessage
{
    private String role;
    private String content;
    
    // Add getters and setters.
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public GptChatMessage(String role, String content)
    {
        this.role = role;
        this.content = content;
    }
}
