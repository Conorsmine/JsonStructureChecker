package com.conorsmine.net.formatter;

/**
 * This builder defines a structure of how a {@link com.google.gson.JsonObject} should be structured.
 */
public class JsonFormatBuilder {

    private JsonFormatBuilder() { }

    /**
     * Entry point and the intended way to create the {@link JsonObjFormatBuilder} for use.
     */
    public static JsonObjFormatBuilder createObjBuilder() {
        return new JsonObjFormatBuilder();
    }


    public enum JsonType {
        U_INT(new TagPredicate.UIntPredicate()),
        INT(new TagPredicate.IntPredicate()),
        U_FLT(new TagPredicate.UFltPredicate()),
        FLT(new TagPredicate.FltPredicate()),
        STR(new TagPredicate.StrPredicate()),
        OBJ(new TagPredicate.ObjPredicate()),
        ARR(new TagPredicate.ArrPredicate());

        final TagPredicate predicate;

        JsonType(TagPredicate predicate) {
            this.predicate = predicate;
        }
    }

}
