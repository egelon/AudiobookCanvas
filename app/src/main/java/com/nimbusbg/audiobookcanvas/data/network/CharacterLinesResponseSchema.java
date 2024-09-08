package com.nimbusbg.audiobookcanvas.data.network;

public class CharacterLinesResponseSchema
{
    private String schema;
    
    public CharacterLinesResponseSchema()
    {
        schema = "{\n" +
                "  \"name\": \"character_lines_response\",\n" +
                "  \"strict\": true,\n" +
                "  \"schema\": {\n" +
                "    \"type\": \"object\",\n" +
                "    \"properties\": {\n" +
                "      \"characterLines\": {\n" +
                "        \"type\": \"array\",\n" +
                "        \"items\": {\n" +
                "          \"type\": \"object\",\n" +
                "          \"properties\": {\n" +
                "            \"line\": {\n" +
                "              \"type\": \"string\"\n" +
                "            },\n" +
                "            \"character\": {\n" +
                "              \"type\": \"string\"\n" +
                "            }\n" +
                "          },\n" +
                "          \"required\": [\n" +
                "            \"line\",\n" +
                "            \"character\"\n" +
                "          ],\n" +
                "          \"additionalProperties\": false\n" +
                "        }\n" +
                "      },\n" +
                "      \"characters\": {\n" +
                "        \"type\": \"array\",\n" +
                "        \"items\": {\n" +
                "          \"type\": \"object\",\n" +
                "          \"properties\": {\n" +
                "            \"character\": {\n" +
                "              \"type\": \"string\"\n" +
                "            },\n" +
                "            \"gender\": {\n" +
                "              \"enum\": [\n" +
                "                \"male\",\n" +
                "                \"female\",\n" +
                "                \"unknown\"\n" +
                "              ]\n" +
                "            }\n" +
                "          },\n" +
                "          \"required\": [\n" +
                "            \"character\",\n" +
                "            \"gender\"\n" +
                "          ],\n" +
                "          \"additionalProperties\": false\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"required\": [\n" +
                "      \"characterLines\",\n" +
                "      \"characters\"\n" +
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
