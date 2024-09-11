package com.nimbusbg.audiobookcanvas.data.network;

public class TextLanguageResponseSchema
{
    private String schema;
    
    public TextLanguageResponseSchema()
    {
        schema = "{\n" +
                "  \"name\": \"text_language_response\",\n" +
                "  \"strict\": true,\n" +
                "  \"schema\": {\n" +
                "    \"type\": \"object\",\n" +
                "    \"properties\": {\n" +
                "      \"language\": {\n" +
                "        \"type\": \"string\",\n" +
                "        \"enum\": [\n" +
                "          \"en_US\",\n" +
                "          \"en_GB\",\n" +
                "          \"fr_FR\",\n" +
                "          \"de_DE\",\n" +
                "          \"es_ES\",\n" +
                "          \"pt_PT\",\n" +
                "          \"it_IT\",\n" +
                "          \"ru_RU\",\n" +
                "          \"bg_BG\",\n" +
                "          \"el_GR\"\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    \"required\": [\n" +
                "      \"language\"\n" +
                "    ],\n" +
                "    \"additionalProperties\": false\n" +
                "  }\n" +
                "}";
    
    }
    
    public String getSchema()
    {
        return schema;
    }
}
