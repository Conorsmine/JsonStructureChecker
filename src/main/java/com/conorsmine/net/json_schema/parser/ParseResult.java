package com.conorsmine.net.json_schema.parser;

import com.conorsmine.net.json_schema.JsonSchema;
import com.conorsmine.net.json_schema.errors.JsonFormatCheckError;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ParseResult {

    private final JsonSchema schema;
    private final Map<String, JsonFormatCheckError> errors;

    ParseResult(JsonSchema schema, @Nullable Map<String, JsonFormatCheckError> errors) {
        this.schema = schema;
        this.errors = errors;
    }

    public Optional<JsonSchema> getSchema() {
        return Optional.ofNullable(schema);
    }

    public Map<String, JsonFormatCheckError> getErrors() {
        return (errors == null) ? new HashMap<>() : errors;
    }

    public boolean hasSchema() {
        return schema != null;
    }
}
