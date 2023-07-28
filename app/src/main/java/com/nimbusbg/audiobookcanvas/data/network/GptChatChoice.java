package com.nimbusbg.audiobookcanvas.data.network;

public class GptChatChoice
{
    private int index;
    private GptChatMessage message;
    private String finish_reason;
    
    // Add getters and setters.
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public GptChatMessage getMessage() {
        return message;
    }
    
    public void setMessage(GptChatMessage message) {
        this.message = message;
    }
    
    public String getFinish_reason() {
        return finish_reason;
    }
    
    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }
}
