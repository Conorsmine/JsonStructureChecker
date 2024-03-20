package com.conorsmine.net.json_schema.parser;

import com.conorsmine.net.json_schema.JsonSchema;
import com.conorsmine.net.json_schema.JsonSchemaBuilder;
import com.conorsmine.net.json_schema.TagType;
import com.conorsmine.net.json_schema.tags.*;

import static com.conorsmine.net.json_schema.TagType.*;

/**
 * The schema for the parser.
 */
class ParserSchema {

    static final String TYPE_STR = "type", DATA_STR = "data";

    private static final TagConditional[] CONDITIONALS = new TagConditional[] {
            // String
            TagConditional.builder()
                    .setNotRequired()
                    .build(STR.name(), DATA_STR, TagObj.builder()
                    .setKeyAs("min_len", TagNumeric.builder(U_INT).setOptional().build())
                    .setKeyAs("max_len", TagNumeric.builder(U_INT).setOptional().build())
                    .build()),

            // Char
            TagConditional.builder()
                    .setNotRequired()
                    .build(CHAR.name(), DATA_STR, TagObj.builder()
                    .setKeyAs("valid_chars", TagArr.builder().setTagFormat(CHAR).setOptional().build())
                    .build()),

            // Numeric
            calcNumericTag(U_INT),
            calcNumericTag(INT),
            calcNumericTag(S_INT),
            calcNumericTag(U_FLT),
            calcNumericTag(FLT),
            calcNumericTag(S_FLT),

            // Boolean
            TagConditional.builder()
                    .setNotRequired()
                    .build(BOOL.name(), DATA_STR, TagObj.builder()
                    .setKeyAs("valid_bools", TagArr.builder().setTagFormat(STR).setOptional().build())
                    .setKeyAs("invalid_bools", TagArr.builder().setTagFormat(STR).setOptional().build())
                    .build()),

            // UUID (No conditionals)

            // Enum
            TagConditional.builder()
                    .setNotRequired()
                    .build(ENUM.name(), DATA_STR, TagArr.builder()
                    .setTagFormat(TagString.builder().build())
                    .build()),

            // Object
            TagConditional.builder()
                    .build(OBJ.name(), DATA_STR, TagArr.builder()
                    .setTagFormat(TagGroup.create("type_def"))
                    .build()),

            // Array
            TagConditional.builder()
                    .build(ARR.name(), DATA_STR, TagObj.builder()
                    .setKeyAs("min_size", TagNumeric.builder(U_INT).setOptional().build())
                    .setKeyAs("max_size", TagNumeric.builder(U_INT).setOptional().build())
                    .setKeyAs("tag_format", TagGroup.create("type_def"))
                    .build()),

            // Group
            TagConditional.builder().build(GROUP.name(), DATA_STR, TagString.builder()
                    .build()),

            // Conditional
            TagConditional.builder()
                    .build(CONDITIONAL.name(), DATA_STR, TagObj.builder()
                    .setKeyAs("reference_key", TagString.builder().build())
                    .setKeyAs("conditionals", TagArr.builder()
                            .setTagFormat(TagObj.builder()
                                    .setKeyAs("reference_value", STR)
                                    .setKeyAs("destination_key", STR)
                                    .setKeyAs("tag_format", TagGroup.create("type_def"))
                                    .build())
                            .build())
                    .build()),

            // Any (No conditionals)
    };

    static final JsonSchema PARSE_JSON_SCHEMA = JsonSchemaBuilder.builder(
            TagObj.builder()
                    .setKeyAs("schema", TagArr.builder().setTagFormat(TagGroup.create("type_def")).build())
                    .setKeyAs("groups", TagArr.builder()
                            .setTagFormat(TagGroup.create("group_def"))
                            .setOptional()
                            .build())
            )
            .addGroup("type_def", TagObj.builder()
                    .setKeyAs("name", STR)
                    .setKeyAs(TYPE_STR, TagEnum.create(TagType.class))
                    .setKeyAs("optional", BOOL)
                    .setConditionalTags(TYPE_STR, CONDITIONALS)
                    .build())
            .addGroup("group_def", TagObj.builder()
                    .setKeyAs("group_name", STR)
                    .setKeyAs("optional", BOOL)
                    .setKeyAs("type_def", TagGroup.create("type_def"))
                    .build())
            .build();



    private static TagConditional calcNumericTag(TagType type) {
        return TagConditional.builder()
                .setNotRequired()
                .build(type.name(), DATA_STR,
                        TagObj.builder()
                                .setKeyAs("min_value", TagNumeric.builder(type).setOptional().build())
                                .setKeyAs("max_value", TagNumeric.builder(type).setOptional().build())
                                .build());
    }
}
