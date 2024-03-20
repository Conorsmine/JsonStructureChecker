package com.conorsmine.net.json_schema;

import com.conorsmine.net.json_schema.tags.JsonTag;
import com.conorsmine.net.json_schema.tags.TagPredicate;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JsonSchema {

    private final Map<String, JsonTag> groupMap;
    private final TagPredicate predicate;

    JsonSchema(TagPredicate predicate) {
        this.predicate = predicate;
        this.groupMap = new HashMap<>();
    }

    JsonSchema(TagPredicate predicate, Map<String, JsonTag> groupMap) {
        this.predicate = predicate;
        this.groupMap = Collections.unmodifiableMap(groupMap);
    }

    public CheckResult check(final @NotNull JsonElement json) {
        final CheckResult.Builder builder = new CheckResult.Builder(groupMap);
        predicate.isValid(json, builder, null);
        return new CheckResult(builder.build().getErrorMsgs());
    }
}
