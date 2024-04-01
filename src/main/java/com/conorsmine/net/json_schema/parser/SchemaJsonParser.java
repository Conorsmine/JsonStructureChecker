package com.conorsmine.net.json_schema.parser;

import com.conorsmine.net.json_schema.CheckResult;
import com.conorsmine.net.json_schema.JsonSchema;
import com.conorsmine.net.json_schema.JsonSchemaBuilder;
import com.conorsmine.net.json_schema.TagType;
import com.conorsmine.net.json_schema.tags.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Parses a JSON object into a schema.
 */
public class SchemaJsonParser {

    public static ParseResult parse(final @NotNull JsonObject json) {
        // Check if the JSON is a valid schema
        final CheckResult result = ParserSchema.PARSE_JSON_SCHEMA.check(json);
        if (!result.isValid()) return new ParseResult(null, result.getErrorMsgs());

        // Create the schema
        return new ParseResult(createSchemaFromJson(json), null);
    }

    private static JsonSchema createSchemaFromJson(JsonObject json) {
        final TagObj.Builder builder = TagObj.builder();
        for (JsonElement jsonElement : json.getAsJsonArray("schema"))
            handleSchemaTag(jsonElement.getAsJsonObject(), builder);

        if (!json.has("groups")) return JsonSchemaBuilder.builder(builder).build();

        final JsonSchemaBuilder schemaBuilder = JsonSchemaBuilder.builder(builder);
        for (JsonElement jsonElement : json.getAsJsonArray("groups"))
            handleGroupTag(jsonElement.getAsJsonObject(), schemaBuilder);

        return schemaBuilder.build();
    }

    private static void handleSchemaTag(JsonObject tag, TagObj.Builder builder) {
        final TagType type = TagType.valueOf(tag.get(ParserSchema.TYPE_STR).getAsString().toUpperCase());
        final String name = tag.get("name").getAsString();
        final boolean optional = TagBoolean.DEFAULT_TRUE.contains(tag.get("optional").getAsString());

        switch (type) {
            case STR:
                builder.setKeyAs(name, TagString.builder()
                        .setMinLen(getOptionalDataOrDefault(tag, "min_len", JsonElement::getAsLong, 0L))
                        .setMaxLen(getOptionalDataOrDefault(tag, "max_len", JsonElement::getAsLong, Long.MAX_VALUE))
                        .setOptional(optional)
                        .build());
                break;
            case CHAR:
                builder.setKeyAs(name, TagChar.builder()
                        .addValidChars(getOptionalDataOrDefault(tag, "valid_chars", (data) ->
                                        data.getAsJsonObject().get("valid_chars").getAsJsonArray().asList().stream()
                                        .map((e) -> e.getAsString().charAt(0))
                                        .collect(Collectors.toList()),
                                new ArrayList<>())
                        )
                        .setOptional(optional)
                        .build());
                break;
            case U_INT:
            case INT:
            case S_INT:
            case U_FLT:
            case FLT:
            case S_FLT:
                builder.setKeyAs(name, TagNumeric.builder(type)
                        .setMinValue(getOptionalDataOrDefault(tag, "min_value", JsonElement::getAsLong, (Long) null))
                        .setMaxValue(getOptionalDataOrDefault(tag, "min_value", JsonElement::getAsLong, (Long) null))
                        .setOptional(optional)
                        .build());
                break;
            case BOOL:
                builder.setKeyAs(name, TagBoolean.builder()
                        .addValidBools(getOptionalDataOrDefault(tag, "valid_bools",
                                (data) -> data.getAsJsonObject().get("valid_bools").getAsJsonArray().asList().stream()
                                        .map(JsonElement::getAsString)
                                        .toArray(String[]::new),
                                new String[0]))
                        .addValidBools(getOptionalDataOrDefault(tag, "valid_bools",
                                (data) -> data.getAsJsonObject().get("valid_bools").getAsJsonArray().asList().stream()
                                        .map(JsonElement::getAsString)
                                        .toArray(String[]::new),
                                new String[0])
                        )
                        .setOptional(optional)
                        .build());
                break;
            case UUID:
                builder.setKeyAs(name, TagUUID.builder().setOptional(optional).build());
                break;
            case ENUM:
                builder.setKeyAs(name, TagEnum.builder()
                        .setEnumConstants(getOptionalDataOrDefault(tag, null,
                                (data) -> data.getAsJsonArray().asList().stream()
                                        .map(JsonElement::getAsString)
                                        .toArray(String[]::new),
                                new String[0]))
                        .setOptional(optional)
                        .build());
                break;
            case OBJ:
                final TagObj.Builder objTagFormat = TagObj.builder();
                for (JsonElement element : tag.get(ParserSchema.DATA_STR).getAsJsonArray())
                    handleSchemaTag(element.getAsJsonObject(), objTagFormat);

                builder.setKeyAs(name, objTagFormat.build());
                break;
            case ARR:
                final TagObj.Builder arrTagFormat = TagObj.builder();
                handleSchemaTag(tag.get("tag_format").getAsJsonObject(), arrTagFormat);

                builder.setKeyAs(name, TagArr.builder()
                        .setMinSize(getOptionalDataOrDefault(tag, "min_size", JsonElement::getAsLong, 0L))
                        .setMaxSize(getOptionalDataOrDefault(tag, "max_size", JsonElement::getAsLong, 0L))
                        .setTagFormat(arrTagFormat.build())
                        .setOptional(optional)
                        .build());
                break;

            case GROUP:
                builder.setKeyAs(name, TagGroup.builder()
                        .setOptional(optional)
                        .build(tag.get("group_name").getAsString()));
                break;

            case ANY:
                builder.setKeyAs(name, TagAny.create(optional));
                break;

            default:
                throw new IllegalStateException("Not implemented yet: " + type);
        }
    }

    private static void handleGroupTag(JsonObject tag, JsonSchemaBuilder builder) {
        final String name = tag.get("group_name").getAsString();
        final boolean optional = TagBoolean.DEFAULT_TRUE.contains(tag.get("optional").getAsString());

        TagObj.Builder groupBuilder = TagObj.builder();
        handleSchemaTag(tag.get("type_def").getAsJsonObject(), groupBuilder);
        builder.addGroup(name, groupBuilder.build());
    }

    private static <T> T getOptionalDataOrDefault(JsonObject json, @Nullable String key, Function<JsonElement, T> supplier, T defaultValue) {
        final JsonElement dataJson = json.get(ParserSchema.DATA_STR);
        if (dataJson == null || !dataJson.isJsonObject()) return defaultValue;

        if (key == null) return supplier.apply(dataJson);

        if (!dataJson.getAsJsonObject().has(key)) return defaultValue;
        return supplier.apply(dataJson.getAsJsonObject().get(key));
    }

}
