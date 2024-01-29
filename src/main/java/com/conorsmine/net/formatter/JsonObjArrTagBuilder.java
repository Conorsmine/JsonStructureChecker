package com.conorsmine.net.formatter;

public class JsonObjArrTagBuilder extends JsonArrTagBuilder {

    final JsonObjFormatBuilder format;

    public JsonObjArrTagBuilder(JsonObjFormatBuilder builderRef, JsonObjFormatBuilder format) {
        super(builderRef, JsonFormatBuilder.JsonType.OBJ);
        this.format = format;
    }

    JsonObjFormatBuilder getFormat() {
        return format;
    }
}
