package com.conorsmine.net.formatter;

public class JsonTagBuilder {

    final JsonObjFormatBuilder builderRef;
    final JsonFormatBuilder.JsonType type;

    boolean isOptional = false;

    JsonTagBuilder(JsonObjFormatBuilder builderRef, JsonFormatBuilder.JsonType type) {
        this.builderRef = builderRef;
        this.type = type;
    }

    public JsonObjFormatBuilder build() {
        return builderRef;
    }

    /**
     * Sets this tag to be optional.
     */
    public JsonTagBuilder setOptional() {
        isOptional = true;
        return this;
    }
}
