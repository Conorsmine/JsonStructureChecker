package com.conorsmine.net.json_schema.errors;

import com.conorsmine.net.json_schema.TagType;

public class JsonMissingTagError extends JsonFormatCheckError {

    public JsonMissingTagError(String path, TagType type) {
        super(String.format("\"%s\" is missing! Please add a %s.", path, type.name()));
    }
}
