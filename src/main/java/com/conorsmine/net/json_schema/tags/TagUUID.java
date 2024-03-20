package com.conorsmine.net.json_schema.tags;

import com.conorsmine.net.json_schema.CheckResult;
import com.conorsmine.net.json_schema.TagType;
import com.conorsmine.net.json_schema.errors.JsonTypeMissmatchError;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TagUUID extends JsonTag {

    private TagUUID(boolean optional) { super(TagType.UUID, optional); }

    public static Builder builder() {
        return new Builder();
    }

    public static TagUUID create(boolean optional) {
        return new TagUUID(optional);
    }

    public static TagUUID create() {
        return create(false);
    }


    // Just here for consistency
    public static class Builder {

        private boolean optional = false;

        private Builder() { }

        public Builder setOptional(boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder setOptional() {
            this.optional = true;
            return this;
        }

        public TagUUID build() {
            return new TagUUID(optional);
        }
    }

    @Override
    public boolean isValid(@NotNull JsonElement tag, @NotNull CheckResult.Builder errorBuilder, @Nullable String path) {
        path = path == null ? "" : path;

        if (!tag.isJsonPrimitive()) {
            errorBuilder.addError(path, new JsonTypeMissmatchError(path, type, tag));
            return false;
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            java.util.UUID.fromString(tag.getAsString());
            return true;
        } catch (IllegalArgumentException e) {
            errorBuilder.addError(path, new JsonTypeMissmatchError(path, type, tag));
            return false;
        }
    }
}
