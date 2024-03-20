package com.conorsmine.net.json_schema;

import com.conorsmine.net.json_schema.tags.*;

public enum TagType {

    STR,
    CHAR,
    U_INT,
    INT,
    S_INT,
    U_FLT,
    FLT,
    S_FLT,
    BOOL,
    UUID,
    ENUM,
    OBJ,
    ARR,
    GROUP,
    CONDITIONAL,
    ANY;    // ANYTHING, LET THERE BE ANYTHING!

    public JsonTag getTag() throws UnsupportedOperationException {
        return TagType.typeToTag(this);
    }

    public static JsonTag typeToTag(TagType type) throws UnsupportedOperationException {
        switch (type) {
            case STR: return TagString.builder().build();
            case CHAR: return TagChar.builder().build();
            case U_INT: return TagNumeric.builder(TagType.U_INT).build();
            case INT: return TagNumeric.builder(TagType.INT).build();
            case S_INT: return TagNumeric.builder(TagType.S_INT).build();
            case U_FLT: return TagNumeric.builder(TagType.U_FLT).build();
            case FLT: return TagNumeric.builder(TagType.FLT).build();
            case S_FLT: return TagNumeric.builder(TagType.S_FLT).build();
            case BOOL: return TagBoolean.builder().build();
            case UUID: return TagUUID.create();
            case ANY: return TagAny.create();

            default:
                throw new UnsupportedOperationException("This method is not implemented for this type! " + type.name());
        }
    }

}