package com.nimbusbg.audiobookcanvas.data.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GptResponseFormat
{
    private String type;
    private JsonObject json_schema;
    
    public GptResponseFormat(String schemaString)
    {
        this.type = "json_schema";
        json_schema = JsonParser.parseString(schemaString).getAsJsonObject();
    }
}
