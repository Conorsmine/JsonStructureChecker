package com.conorsmine.net.json_schema.tags;

import com.conorsmine.net.json_schema.CheckResult;
import com.conorsmine.net.json_schema.TagType;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TagAny extends JsonTag {
    private TagAny(boolean optional) {
        super(TagType.ANY, optional);
    }

    public static TagAny create() {
        return new TagAny(false);
    }

    public static TagAny create(boolean optional) {
        return new TagAny(optional);
    }

    @Override
    public boolean isValid(@NotNull JsonElement tag, @NotNull CheckResult.Builder errorBuilder, @Nullable String path) {
        return true;
    }
}
