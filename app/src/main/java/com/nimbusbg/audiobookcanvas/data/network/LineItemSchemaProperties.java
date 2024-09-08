package com.nimbusbg.audiobookcanvas.data.network;

import java.util.List;

public class LineItemSchemaProperties
{
    private String type;
    private LineProperties properties;
    private List<String> required;
    private boolean additionalProperties;
    
    public LineItemSchemaProperties() {
        this.type = "object";
        this.properties = new LineProperties();
        this.required = List.of(new String[]{"line", "character"});
        this.additionalProperties = false;
    }
}
