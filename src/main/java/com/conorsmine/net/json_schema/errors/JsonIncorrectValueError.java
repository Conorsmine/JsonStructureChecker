package com.conorsmine.net.json_schema.errors;

import com.conorsmine.net.json_schema.TagType;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;

public class JsonIncorrectValueError extends JsonFormatCheckError {

    public JsonIncorrectValueError(String path, @NotNull JsonElement tag, @NotNull TagType type, @NotNull Collection<? extends Serializable> validValues) {
        super(String.format(
                "The value \"%s\" is incorrect for \"%s\". \"%s\" should be one of the following: [%s%s].",
                tag.getAsString(),
                type.name(),
                path,
                validValues.stream().limit(5).map(Serializable::toString).reduce((a, b) -> a + ", " + b).orElse(""),
                validValues.size() > 5 ? ", ..." : ""
        ));
    }

}
