package com.conorsmine.net.json_schema.errors;

import com.conorsmine.net.json_schema.TagType;
import com.google.gson.JsonElement;

public class JsonTypeMissmatchError extends JsonFormatCheckError {

    public JsonTypeMissmatchError(String path, TagType shouldType, JsonElement json) {
        super(String.format("The element \"%s\" is not of type %s.", path, shouldType.name()));
    }
}
