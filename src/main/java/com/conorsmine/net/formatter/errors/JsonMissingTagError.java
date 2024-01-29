package com.conorsmine.net.formatter.errors;

import com.conorsmine.net.formatter.JsonFormatBuilder;

public class JsonMissingTagError extends JsonFormatCheckError {

    public JsonMissingTagError(String path, JsonFormatBuilder.JsonType type) {
        super(String.format("\"%s\" is missing! Please add a %s.", path, type.name()));
    }
}
