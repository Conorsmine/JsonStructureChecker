package com.conorsmine.net.formatter.errors;

import com.conorsmine.net.formatter.JsonFormatBuilder;
import com.google.gson.JsonElement;

public class JsonTypeMissmatchError extends JsonFormatCheckError {

    public JsonTypeMissmatchError(String path, JsonFormatBuilder.JsonType shouldType, JsonElement json) {
        super(String.format("The element \"%s\" is not of type %s.\n\"%s\"", path, shouldType.name(), json.toString()));
    }
}
