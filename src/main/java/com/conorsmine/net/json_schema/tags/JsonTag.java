package com.conorsmine.net.json_schema.tags;

import com.conorsmine.net.json_schema.TagType;

public abstract class JsonTag implements TagPredicate {

    final TagType type;

    private final boolean optional;

    JsonTag(TagType type, boolean optional) {
        this.type = type;
        this.optional = optional;
    }

    public boolean isOptional() {
        return optional;
    }

    public TagType getType() {
        return type;
    }

}
