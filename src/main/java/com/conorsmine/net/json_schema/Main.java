package com.conorsmine.net.json_schema;

import com.conorsmine.net.json_schema.tags.TagObj;
import com.google.gson.JsonParser;

public class Main {

    public static void main(String[] args) {
        String check = "{\n" +
                "  \"meta_data\": {\n" +
                "    \"parser_version\": \"1.0\"\n" +
                "  },\n" +
                "  \"weapon_data\": {\n" +
                "    \"id\": \"4d4e21ca-5534-43d1-a0d6-91d485f759d4\",\n" +
                "    \"name\": \"Sword\",\n" +
                "    \"attack_speed\": 1.8,\n" +
                "    \"range\": 1\n" +
                "  }\n" +
                "}";

        final JsonSchema schema = JsonSchemaBuilder.createSchema(TagObj.builder()
                .setKeyAs("meta_data", TagObj.builder()
                        .setKeyAs("parser_version", TagType.STR)
                        .build())
                .setKeyAs("weapon_data", TagObj.builder()
                        .setKeyAs("id", TagType.UUID)
                        .setKeyAs("name", TagType.STR)
                        .setKeyAs("attack_speed", TagType.U_FLT)
                        .setKeyAs("range", TagType.U_INT)
                        .build())
        );

        final CheckResult result = schema.check(JsonParser.parseString(check));
        result.get
    }

}
