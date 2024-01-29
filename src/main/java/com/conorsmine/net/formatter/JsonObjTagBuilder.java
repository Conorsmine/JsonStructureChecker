package com.conorsmine.net.formatter;

public class JsonObjTagBuilder extends JsonTagBuilder {

    final JsonObjFormatBuilder format;

    JsonObjTagBuilder(JsonObjFormatBuilder builderRef, JsonObjFormatBuilder format) {
        super(builderRef, JsonFormatBuilder.JsonType.OBJ);
        this.format = format;
    }

    JsonObjFormatBuilder getFormat() {
        return format;
    }
}
