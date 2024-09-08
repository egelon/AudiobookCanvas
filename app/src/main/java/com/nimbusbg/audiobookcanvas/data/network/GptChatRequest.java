package com.nimbusbg.audiobookcanvas.data.network;

import java.util.List;

public class GptChatRequest
{
    String model;
    private List<GptChatMessage> messages;
    
    int temperature;
    int max_tokens;
    int top_p;
    double frequency_penalty;
    double presence_penalty;
    
    private GptResponseFormat response_format;
    
    // Add getters and setters.
    
    public String getModel()
    {
        return model;
    }
    
    public void setModel(String model)
    {
        this.model = model;
    }
    
    public List<GptChatMessage> getMessages()
    {
        return messages;
    }
    
    public void setMessages(List<GptChatMessage> messages)
    {
        this.messages = messages;
    }
    
    public int getTemperature()
    {
        return temperature;
    }
    
    public void setTemperature(int temperature)
    {
        this.temperature = temperature;
    }
    
    public int getMax_tokens()
    {
        return max_tokens;
    }
    
    public void setMax_tokens(int max_tokens)
    {
        this.max_tokens = max_tokens;
    }
    
    public int getTop_p()
    {
        return top_p;
    }
    
    public void setTop_p(int top_p)
    {
        this.top_p = top_p;
    }
    
    public double getFrequency_penalty()
    {
        return frequency_penalty;
    }
    
    public void setFrequency_penalty(double frequency_penalty)
    {
        this.frequency_penalty = frequency_penalty;
    }
    
    public double getPresence_penalty()
    {
        return presence_penalty;
    }
    
    public void setPresence_penalty(double presence_penalty)
    {
        this.presence_penalty = presence_penalty;
    }
    
    public GptResponseFormat getResponse_format()
    {
        return response_format;
    }
    
    public void setResponse_format(GptResponseFormat response_format)
    {
        this.response_format = response_format;
    }
}
