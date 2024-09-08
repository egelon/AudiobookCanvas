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
                "          \"en\",\n" +
                "          \"fr\",\n" +
                "          \"de\",\n" +
                "          \"es\",\n" +
                "          \"pt\",\n" +
                "          \"it\",\n" +
                "          \"ru\",\n" +
                "          \"pl\",\n" +
                "          \"ja\",\n" +
                "          \"zh\",\n" +
                "          \"ko\",\n" +
                "          \"bg\",\n" +
                "          \"el\",\n" +
                "          \"sr\",\n" +
                "          \"da\",\n" +
                "          \"sv\",\n" +
                "          \"fi\",\n" +
                "          \"no\",\n" +
                "          \"nl\",\n" +
                "          \"he\"\n" +
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
