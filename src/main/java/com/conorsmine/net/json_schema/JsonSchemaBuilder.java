package com.conorsmine.net.json_schema;

import com.conorsmine.net.json_schema.parser.ParseResult;
import com.conorsmine.net.json_schema.parser.SchemaJsonParser;
import com.conorsmine.net.json_schema.tags.JsonTag;
import com.conorsmine.net.json_schema.tags.TagArr;
import com.conorsmine.net.json_schema.tags.TagBuilder;
import com.conorsmine.net.json_schema.tags.TagObj;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class JsonSchemaBuilder {

    public static ParseResult createSchemaFromJson(final @NotNull JsonObject json) {
        return SchemaJsonParser.parse(json);
    }

    /**
     * Create a schema from a tag builder.
     * @param tagSchema the builder for the tag
     * @return the schema
     * @see TagObj#builder()
     * @see TagArr#builder()
     */
    public static JsonSchema createSchema(final @NotNull TagBuilder<?> tagSchema) {
        return new JsonSchema(tagSchema.build());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Builder methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Create a schema from a tag builder.
     * @param tagSchema the builder for the tag
     * @return the schema
     * @see TagObj#builder()
     * @see TagArr#builder()
     */
    public static JsonSchemaBuilder builder(final @NotNull TagBuilder<?> tagSchema) {
        return new JsonSchemaBuilder(tagSchema);
    }

    private final Map<String, JsonTag> groupMap = new HashMap<>();
    private final TagBuilder<?> tagSchema;

    private JsonSchemaBuilder(final TagBuilder<?> tagSchema) {
        this.tagSchema = tagSchema;
    }

    /**
     * Add a group to the schema.
     * @param groupName the name of the group
     * @param tagSchema the schema for the group
     * @return this builder
     */
    public JsonSchemaBuilder addGroup(final @NotNull String groupName, final @NotNull JsonTag tagSchema) {
        groupMap.put(groupName.toLowerCase(), tagSchema);
        return this;
    }

    public JsonSchema build() {
        return new JsonSchema(tagSchema.build(), groupMap);
    }
}
